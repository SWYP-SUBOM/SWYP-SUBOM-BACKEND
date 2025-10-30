package swyp_11.ssubom.domain.notification.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import swyp_11.ssubom.domain.writing.entity.Post;
import swyp_11.ssubom.global.security.entity.User;

import java.time.Instant;

@Entity
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notifications_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 스키마가 문자열을 요구
    @Column(name = "reaction_type", length = 10)
    private String reactionType;

    @Column(name = "actor_count")
    private Long actorCount;

    @Column(name = "is_read")
    private Boolean read;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
