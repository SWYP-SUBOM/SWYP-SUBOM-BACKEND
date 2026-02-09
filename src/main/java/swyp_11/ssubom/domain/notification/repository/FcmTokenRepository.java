package swyp_11.ssubom.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyp_11.ssubom.domain.notification.entity.FcmToken;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByUserIdAndTokenAndIsActiveTrue(Long userId, String token);

    List<FcmToken> findByUserIdAndIsActiveTrue(Long userId);

    @Modifying
    @Query("UPDATE FcmToken f SET f.isActive = false WHERE f.userId = :userId AND f.token = :token")
    void deactivateToken(@Param("userId") Long userId, @Param("token") String token);
}
