package swyp_11.ssubom.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.notification.entity.FcmToken;
import swyp_11.ssubom.domain.notification.repository.FcmTokenRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private static final int MAX_RETRY_COUNT = 3;  // 최대 재시도 횟수
    private static final long RETRY_DELAY_MS = 1000;  // 재시도 간격 (1초)

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveToken(Long userId, String token) {
        // 이미 등록된 토큰인지 확인
        Optional<FcmToken> existing = fcmTokenRepository
                .findByUserIdAndTokenAndIsActiveTrue(userId, token);

        if (existing.isPresent()) {
            log.info("Token already registered for user: {}", userId);
            return;
        }

        FcmToken fcmToken = FcmToken.create(userId, token);
        fcmTokenRepository.save(fcmToken);

        log.info("FCM token registered - userId: {}", userId);
    }

    @Transactional
    public void deleteToken(Long userId, String token) {
        fcmTokenRepository.deactivateToken(userId, token);
        log.info("FCM token deactivated - userId: {}", userId);
    }

    public void sendPushNotification(Long userId, String content) {
        List<FcmToken> tokens = fcmTokenRepository.findByUserIdAndIsActiveTrue(userId);

        if (tokens.isEmpty()) {
            log.debug("No active FCM tokens for user: {}", userId);
            return;
        }

        tokens.forEach(fcmToken -> {
            boolean success = sendWithRetry(fcmToken, content);

            if (!success) {
                log.error("Failed to send push after {} retries - userId: {}, token: {}",
                        MAX_RETRY_COUNT, userId, maskToken(fcmToken.getToken()));
            }
        });
    }

    /**
     * 재시도 로직을 포함한 푸시 전송
     */
    private boolean sendWithRetry(FcmToken fcmToken, String content) {
        for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
            try {
                sendToToken(fcmToken.getToken(), content);

                if (attempt > 1) {
                    log.info("Push sent successfully on attempt {}/{}", attempt, MAX_RETRY_COUNT);
                }
                return true;  // 성공

            } catch (Exception e) {
                // 만료된 토큰인 경우 즉시 비활성화하고 재시도 중단
                if (isInvalidTokenError(e)) {
                    log.warn("Invalid token detected, deactivating - attempt {}/{}",
                            attempt, MAX_RETRY_COUNT);
                    deactivateInvalidToken(fcmToken);
                    return false;  // 재시도 중단
                }

                // 만료된 토큰이 아닌 경우 재시도
                log.warn("Push send failed (attempt {}/{}) - error: {}",
                        attempt, MAX_RETRY_COUNT, e.getMessage());

                // 마지막 시도가 아니면 대기 후 재시도
                if (attempt < MAX_RETRY_COUNT) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry interrupted", ie);
                        return false;
                    }
                } else {
                    log.error("All retry attempts failed - final error: {}", e.getMessage());
                }
            }
        }

        return false;
    }

    @Transactional
    public void deactivateInvalidToken(FcmToken fcmToken) {
        fcmToken.deactivate();
        fcmTokenRepository.save(fcmToken);
    }

    private void sendToToken(String token, String content)
            throws FirebaseMessagingException {

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(content)
                        .setBody(content)
                        .build())
                .putData("timestamp", String.valueOf(System.currentTimeMillis()))
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.debug("Push sent: {}", response);
    }

    private boolean isInvalidTokenError(Exception e) {
        if (!(e instanceof FirebaseMessagingException)) {
            return false;
        }

        FirebaseMessagingException fme = (FirebaseMessagingException) e;
        String errorCode = fme.getMessagingErrorCode() != null
                ? fme.getMessagingErrorCode().name()
                : "";

        return "UNREGISTERED".equals(errorCode)
                || "INVALID_ARGUMENT".equals(errorCode);
    }

    /**
     * 토큰 마스킹 (로그 보안)
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "...";
    }
}