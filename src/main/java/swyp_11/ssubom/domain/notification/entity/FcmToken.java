package swyp_11.ssubom.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public void deactivate() {
        this.isActive = false;
    }

    @Builder
    public FcmToken(Long id, Long userId, String token, Boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.isActive = isActive;
    }

    public static FcmToken create(Long userId, String token) {
        return FcmToken.builder()
                .userId(userId)
                .token(token)
                .isActive(true)
                .build();
    }
}
