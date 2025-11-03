package swyp_11.ssubom.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;
import swyp_11.ssubom.domain.post.entity.Post;
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

    @Column(name = "reaction_type", length = 10)
    private String reactionType;

    @Column(name = "actor_count")
    private Long actorCount;

    @Column(name = "is_read", nullable = false, columnDefinition = "boolean default false")
    private boolean isRead;
}
