<<<<<<<< HEAD:src/main/java/swyp_11/ssubom/domain/entity/User.java
package swyp_11.ssubom.domain.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
========
package swyp_11.ssubom.global.security.entity;


>>>>>>>> develop:src/main/java/swyp_11/ssubom/global/security/entity/User.java
import jakarta.persistence.*;
import lombok.Data;
import swyp_11.ssubom.domain.user.entity.Streak;

import java.time.LocalDateTime;

@Getter
@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public Long userId;

    @Column(name = "kakao_id", length = 255)
    private String kakaoId;

    @Column(name = "user_name", length = 255)
    public String userName;

    @Column(name = "email", length = 255)
    public String email;

    @Column(name = "role", length = 255)
    public String role;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Streak streak;
}




