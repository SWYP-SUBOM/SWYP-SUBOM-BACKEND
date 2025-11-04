package swyp_11.ssubom.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.global.security.entity.RefreshEntity;
import swyp_11.ssubom.domain.user.repository.RefreshRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    @Transactional
    public void saveRefresh(String kakaoId, String refreshToken , Integer expireS) {
        logger.info("saveRefresh 호출됨. kakaoId: {}, refreshToken: {}, expiration: {}",
                kakaoId, refreshToken, new Date(System.currentTimeMillis() + expireS * 1000L));
        long expireAtMillis = System.currentTimeMillis() + expireS * 1000L;
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .kakaoId(kakaoId)
                .refreshValue(refreshToken)
                .expiration( LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(expireAtMillis),
                        ZoneId.of("Asia/Seoul")
                ))
                .build();


        refreshRepository.save(refreshEntity);
    }

    @Transactional
    @Scheduled(cron = "0 0 2,10,19 * * *", zone = "Asia/Seoul")
    public void cleanExpiredTokens() {
        long deleted = refreshRepository.deleteByExpirationBefore(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }
}
