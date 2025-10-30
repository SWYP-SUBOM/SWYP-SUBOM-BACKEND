package swyp_11.ssubom.global.security.repository;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.global.security.entity.RefreshEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity,Long> {
    List<RefreshEntity> findByKakaoId(String kakaoId);

    Boolean existsByRefreshValue(String refreshValue);

    @Transactional
    void deleteByRefreshValue(String refreshValue);

    long deleteByExpirationBefore(LocalDateTime now);
}
