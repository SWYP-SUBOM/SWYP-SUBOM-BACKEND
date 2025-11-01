package swyp_11.ssubom.user.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import java.time.Instant;

@Getter
@Entity
@Table(name = "user") // 테이블명이 소문자 'user'
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "kakao_id", length = 255)
    private String kakaoId;

    @Column(name = "role", length = 255)
    private String role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "user_name", length = 255)
    private String userName;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Streak streak;

}