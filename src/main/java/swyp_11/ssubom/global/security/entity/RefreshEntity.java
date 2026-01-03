package swyp_11.ssubom.global.security.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swyp_11.ssubom.domain.common.BaseTimeEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private Long refreshId;

    @Column(name = "kakao_id", length = 255)
    private String kakaoId;

    @Column(name = "refresh_value", length = 512)
    private String refreshValue;

    private LocalDateTime expiration;

    @Builder
    public RefreshEntity(String kakaoId, String refreshValue, LocalDateTime expiration ) {
        this.kakaoId = kakaoId;
        this.refreshValue = refreshValue;
        this.expiration = expiration;
    }
}
