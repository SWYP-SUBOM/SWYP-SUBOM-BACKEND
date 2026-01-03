package swyp_11.ssubom.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.ReactionType;
import swyp_11.ssubom.domain.user.entity.User;

@Getter
@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_type_id")
    private ReactionType reactionType;

    @Column(name = "actor_count")
    private Long actorCount;

    @Column(name = "is_read", nullable = false, columnDefinition = "boolean default false")
    private boolean isRead;

    @Builder
    public Notification(User receiver, Post post, ReactionType reactionType, Long actorCount) {
        this.receiver = receiver;
        this.post = post;
        this.reactionType = reactionType;
        this.actorCount = actorCount == null ? 1L : actorCount;
    }

    public static Notification of(User receiver, Post post, ReactionType type, long actorCount) {
        return Notification.builder()
                .receiver(receiver)
                .post(post)
                .reactionType(type)
                .actorCount(actorCount)
                .build();
    }

    public void markAsUnread() {
        this.isRead = false;
    }

    public void setActorCount(long totalCount) {
        this.actorCount = totalCount;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
