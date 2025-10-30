package swyp_11.ssubom.domain.user.entity;

import jakarta.persistence.*;
import swyp_11.ssubom.global.security.entity.User;

@Entity
public class Streak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "streak_id")
    private Long id;

    @Column(name = "streak_count")
    private Long streakCount;

    @Column(name = "challenger_count")
    private Long challengerCount;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
