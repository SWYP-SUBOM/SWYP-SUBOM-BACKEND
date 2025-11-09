package swyp_11.ssubom.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.user.entity.Streak;
import swyp_11.ssubom.domain.user.repository.StreakRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreakScheduler {

    private final StreakRepository streakRepository;

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
}