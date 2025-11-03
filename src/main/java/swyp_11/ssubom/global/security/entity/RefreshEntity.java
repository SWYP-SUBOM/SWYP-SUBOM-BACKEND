package swyp_11.ssubom.global.security.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private Long refreshId;

    @Column(name = "kakao_id", length = 255)
    private String kakaoId;

    @Column(name = "refresh_value", length = 255)
    private String refreshValue;

    private LocalDateTime expiration;

    @Builder
    public RefreshEntity(String kakaoId, String refresh, LocalDateTime expiration ) {
        this.kakaoId = kakaoId;
        this.refreshValue = refresh;
        this.expiration = expiration;
    }
}
