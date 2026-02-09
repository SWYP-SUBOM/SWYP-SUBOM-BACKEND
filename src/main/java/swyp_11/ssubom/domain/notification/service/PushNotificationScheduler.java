package swyp_11.ssubom.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificationScheduler {
    private final PostRepository postRepository;  // 글 작성 확인용
    private final UserRepository userRepository;  // 전체 유저 조회용
    private final FcmService fcmService;

    /**
     * 매일 오전 9시에 실행
     * cron: 초 분 시 일 월 요일
     */
//    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    // test용 3분에 한 번 실행
    @Scheduled(fixedRate = 180000)
    public void sendDailyWritingReminder() {
        log.info("=== Push notification started ===");

        LocalDate today = LocalDate.now();
        List<Long> usersWhoWroteToday = getUsersWhoWroteToday(today);
        List<User> activeUsers = userRepository.findAllByIsDeletedFalse();

        int sentCount = 0;

        for (User user : activeUsers) {
            // 오늘 글 쓴 유저는 제외
            if (usersWhoWroteToday.contains(user.getUserId())) {
                continue;
            }

            try {
                fcmService.sendPushNotification(
                        user.getUserId(),
                        "오늘의 주제가 열렸어요!\n써봄과 함께 사고력 훈련하러 가볼까요?"
                );
                sentCount++;
            } catch (Exception e) {
                log.error("Failed to send push to user: {}", user.getUserId(), e);
            }
        }

        log.info("=== Push Notification sent to {} users ===", sentCount);
    }

    /**
     * 오늘 글을 작성한 유저 ID 목록 조회
     */
    private List<Long> getUsersWhoWroteToday(LocalDate today) {
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        return postRepository.findUserIdsWhoWroteBetween(startOfDay, endOfDay);
    }
}
