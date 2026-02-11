package swyp_11.ssubom.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.domain.user.entity.Streak;
import swyp_11.ssubom.domain.user.repository.StreakRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreakScheduler {

    private final StreakRepository streakRepository;
    private final PostRepository postRepository;

    /**
     * 매달 1일 00시 정각에 모든 유저의 challengerCount 초기화
     */
    @Transactional
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyChallenge() {
        List<Streak> allStreaks = streakRepository.findAll();
        allStreaks.forEach(Streak::resetMonthly);
        streakRepository.saveAll(allStreaks);
        log.info("[{}] 기준으로 총 {}명의 사용자의 챌린저 카운트를 초기화했습니다.", LocalDate.now(), allStreaks.size());
    }

    /**
     * 매일 00시 정각에 전날 글 발행하지 않은 유저의 streakCount를 0으로 초기화
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void resetStreakCountIfNoPublishedYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(23, 59, 59);

        List<Streak> allStreaks = streakRepository.findAll();
        List<Long> resetUserIds = new ArrayList<>();

        for (Streak streak : allStreaks) {
            Long userId = streak.getUser().getUserId();

            // 전날 PUBLISHED 된 글이 있는지 확인
            boolean hasPublishedYesterday = postRepository
                    .existsByUserIdAndPublishedTrueAndCreatedAtBetween(
                            userId,
                            startOfYesterday,
                            endOfYesterday
                    );

            if (!hasPublishedYesterday) {
                streak.resetStreakCount();  // 0으로 초기화
                resetUserIds.add(userId);
            }
        }

        if (!resetUserIds.isEmpty()) {
            log.info("[{}] 전날 글 미발행으로 {}명의 streakCount를 초기화했습니다. (userIds: {})",
                    LocalDate.now(), resetUserIds.size(), resetUserIds);
        } else {
            log.info("[{}] 전날 글 미발행 유저 없음 - streakCount 초기화 없음", LocalDate.now());
        }
    }
}