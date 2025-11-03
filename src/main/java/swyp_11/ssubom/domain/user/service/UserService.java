package swyp_11.ssubom.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.user.dto.StreakResponse;
import swyp_11.ssubom.domain.user.repository.StreakRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final StreakRepository streakRepository;

    public StreakResponse getStreak(Long userId) {
        return streakRepository.findByUser_UserId(userId)
                .map(streak -> StreakResponse.toDto(streak.getStreakCount()))
                .orElseGet(() -> StreakResponse.toDto(0L));
    }
}
