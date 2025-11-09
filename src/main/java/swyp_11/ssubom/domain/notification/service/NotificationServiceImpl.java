package swyp_11.ssubom.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swyp_11.ssubom.domain.notification.entity.Notification;
import swyp_11.ssubom.domain.notification.repository.NotificationRepository;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.ReactionType;
import swyp_11.ssubom.domain.user.entity.User;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService{
    private static final long TIMEOUT = 1000L * 60 * 60; // 1시간 유지

    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 로그인 시 SSE 연결
     */
    @Override
    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.put(userId, emitter);
        log.info("[SSE 연결 성공] userId={}", userId);

        sendSnapshot(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        return emitter;
    }

    /**
     * 반응 클릭 시 알림 발생
     * - 자기 글에 누른 경우는 알림 제외
     * - 기존 알림(같은 post + reactionType) 있으면 actorCount +1
     */
    @Override
    @Transactional
    public void createReactionNotification(Post post, User actor, ReactionType type) {
        User receiver = post.getUser();
        if (receiver.getUserId().equals(actor.getUserId())) return;

        Notification notification = notificationRepository
                .findByReceiverAndPostAndReactionType(receiver, post, type)
                .map(n -> {
                    n.increaseActorCount();
                    return n;
                })
                .orElseGet(() -> {
                    Notification newOne = Notification.of(receiver, post, type, 1L);
                    return notificationRepository.save(newOne);
                });

        sendNotification(notification);
    }

    /**
     * 최초 연결 시 unreadCount 전달
     */
    private void sendSnapshot(Long userId, SseEmitter emitter) {
        long unreadCount = notificationRepository.countByReceiver_UserIdAndIsReadFalse(userId);
        sendEvent(emitter, "snapshot", Map.of("unreadCount", unreadCount));
    }

    /**
     * 새 알림 전송
     */
    @Transactional
    public void sendNotification(Notification notification) {
        Long userId = notification.getReceiver().getUserId();
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            sendEvent(emitter, "newNotification", toDto(notification));
            log.info("[알림 전송] → userId={}, postId={}, reactionType={}",
                    userId, notification.getPost().getPostId(), notification.getReactionType());
        }
    }

    private Map<String, Object> toDto(Notification n) {
        return Map.of(
                "id", n.getId(),
                "postId", n.getPost().getPostId(),
                "reactionType", n.getReactionType(),
                "actorCount", n.getActorCount(),
                "isRead", n.isRead(),
                "createdAt", n.getCreatedAt()
        );
    }

    private void sendEvent(SseEmitter emitter, String event, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(event)
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}
