package swyp_11.ssubom.domain.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @Column(unique = true ,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    private boolean is2faEnabled;

    private String totpSecret;

    private int failedLoginCount;

    private LocalDateTime lockedUntil;

    public void increaseFail() {
        this.failedLoginCount++;
        if (failedLoginCount >= 5) {
            this.lockedUntil = LocalDateTime.now().plusMinutes(15);
        }
    }

    public void resetFail() {
        this.failedLoginCount = 0;
        this.lockedUntil = null;
    }

    public void enable2fa(String secret) {
        this.is2faEnabled = true;
        this.totpSecret = secret;
    }
}
