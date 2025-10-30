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
    private Long refreshId;
    private String kakaoId;
    private String refreshValue;
    private LocalDateTime expiration;

    @Builder
    public RefreshEntity(String kakaoId, String refresh, LocalDateTime expiration ) {
        this.kakaoId = kakaoId;
        this.refreshValue = refresh;
        this.expiration = expiration;
    }
}
