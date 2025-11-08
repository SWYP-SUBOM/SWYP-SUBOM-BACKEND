package swyp_11.ssubom.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Long weeklyChallengeCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder
    private Streak(Long streakCount, Long weeklyChallengeCount, User user) {
        this.streakCount = streakCount == null ? 0L : streakCount;
        this.weeklyChallengeCount = weeklyChallengeCount == null ? 0L : weeklyChallengeCount;
        this.user = user;
    }

    public static Streak create(User user) {
        return Streak.builder()
                .user(user)
                .streakCount(1L)
                .weeklyChallengeCount(0L)
                .build();
    }

    public void increaseDaily(boolean alreadyPostedToday) {
        if (!alreadyPostedToday) this.streakCount++;
    }

    public void updateWeeklyChallenge(long publishedCountThisWeek) {
        if (publishedCountThisWeek >= 5) this.weeklyChallengeCount++;
    }

    public void resetMonthly() {
        this.weeklyChallengeCount = 0L;
    }
}
