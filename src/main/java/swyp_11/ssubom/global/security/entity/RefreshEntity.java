package swyp_11.ssubom.global.security.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String expiration;

    @Builder
    public RefreshEntity(String kakaoId, String refresh, String expiration) {
        this.kakaoId = kakaoId;
        this.refreshValue = refresh;
        this.expiration = expiration;
    }
}
