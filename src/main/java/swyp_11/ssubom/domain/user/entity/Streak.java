package swyp_11.ssubom.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;

@Getter
@Entity
@Table(name = "streak")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Streak extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "streak_id")
    private Long id;

    @Column(name = "streak_count")
    private Long streakCount;

    @Column(name = "challenger_count")
    private Long challengerCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
