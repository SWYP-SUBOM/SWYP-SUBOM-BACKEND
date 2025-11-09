package swyp_11.ssubom.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import swyp_11.ssubom.domain.user.dto.StreakResponse;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.user.repository.RefreshRepository;
import swyp_11.ssubom.domain.user.repository.StreakRepository;
import swyp_11.ssubom.domain.user.repository.UserRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.security.entity.RefreshEntity;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final StreakRepository streakRepository;
    @Qualifier("kakaoUnlinkWebClient")
    private final WebClient kakaoClient;
    private final UserRepository userRepository;
   private final RefreshRepository refreshRepository;

    public StreakResponse getStreak(Long userId) {
        return streakRepository.findByUser_UserId(userId)
                .map(streak -> StreakResponse.toDto(streak.getStreakCount()))
                .orElseGet(() -> StreakResponse.toDto(0L));
    }

    public void unregisterUser(String targetId){
        String body = String.format("target_id_type=user_id&target_id=%s", targetId);

        try {
            kakaoClient.post()
                    .uri("/v1/user/unlink")
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.createException().map(
                                    e -> new BusinessException(ErrorCode.UNREGISTER_FAILED)
                            ))
                    .toBodilessEntity()
                    .block();
        }catch (BusinessException e){
            throw new BusinessException(ErrorCode.UNREGISTER_FAILED);
        }
        }


    @Transactional
    public void userDelete(String targetId) {
        unregisterUser(targetId);
        List<RefreshEntity> refreshes = refreshRepository.findByKakaoId(targetId);
        if (!refreshes.isEmpty()) {
            refreshRepository.deleteAll(refreshes);
        }
        User user = userRepository.findByKakaoId(targetId);

        if (user != null) {
            user.deleteUser(null,true);
        }
    }
}
