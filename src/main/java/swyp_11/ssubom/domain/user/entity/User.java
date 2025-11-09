package swyp_11.ssubom.domain.user.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

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

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Streak streak;

    @Builder
    public User(Long userId, String kakaoId, String userName, String email, String role, Streak streak) {
        this.userId = userId;
        this.kakaoId = kakaoId;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.streak = streak;
    }

    public User(String kakaoId, String userName, String email, String role) {
        this.kakaoId = kakaoId;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void deleteUser(String kakaoId,boolean isDeleted) {
        this.kakaoId = kakaoId;
        this.isDeleted = isDeleted;
    }

    public boolean isSame(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }
}