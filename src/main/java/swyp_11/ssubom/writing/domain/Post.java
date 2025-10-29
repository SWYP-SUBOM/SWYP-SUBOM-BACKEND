package swyp_11.ssubom.writing.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "status", length = 20)
    private String status; //DRAFT, PUBLISHED

    @Column(name = "is_revised")
    private boolean isRevised;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Builder
    public Post(User user, Topic topic, String content, String status, boolean isRevised) {
        this.user = user;
        this.topic = topic;
        this.content = content;
        this.status = status.toUpperCase(); // Ensure status is consistent
        this.isRevised = isRevised;
        // Timestamps are handled by @PrePersist
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
