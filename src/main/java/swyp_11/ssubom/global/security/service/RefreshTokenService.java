package swyp_11.ssubom.global.security.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.global.security.entity.RefreshEntity;
import swyp_11.ssubom.global.security.repository.RefreshRepository;

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

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .kakaoId(kakaoId)
                .refresh(refreshToken)
                .expiration(new Date(System.currentTimeMillis() + expireS * 1000L).toString())
                .build();
        // 로그 추가: 저장할 객체 출력
        logger.info("저장할 RefreshEntity: {}", refreshEntity);

        refreshRepository.save(refreshEntity);
        // 저장 완료 후 로그
        logger.info("RefreshEntity 저장 완료, kakaoId: {}", kakaoId);
    }
}
