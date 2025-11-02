package swyp_11.ssubom.writing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.topic.entity.Topic;
import swyp_11.ssubom.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PostStatus status; //DRAFT, PUBLISHED

    @Column(name = "is_revised")
    private boolean isRevised;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "nickname", length = 100, unique = true)
    private String nickname;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AIFeedback aiFeedback;

    @Builder(access = AccessLevel.PRIVATE)
    public Post(User user, Topic topic, String content, PostStatus status, String nickname) {
        this.user = user;
        this.topic = topic;
        this.content = content;
        this.status = status; // Ensure status is consistent
        this.isRevised = false;
        this.nickname = nickname;
    }

    public static Post create(User user, Topic topic, String content, PostStatus status, String nickname) {
        if (user == null || topic == null) {
            throw new IllegalArgumentException("User와 Topic은 필수입니다.");
        }

        return Post.builder()
                .user(user)
                .topic(topic)
                .content(content)
                .status(status)
                .nickname(nickname)
                .build();
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


    public void update(PostStatus nextStatus, String content) {
        if (this.status == PostStatus.DRAFT) {
            updateFromDraft(nextStatus, content);
        } else if (this.status == PostStatus.PUBLISHED) {
            updateFromPublished(nextStatus, content);
        }
    }

    public void updateFromDraft(PostStatus nextStatus, String content) {
        if (nextStatus == PostStatus.DRAFT) {
            // case C
            if (this.content != null && this.content.equals(content)) {
                return;
            }
            this.content = content;
        } else if (nextStatus == PostStatus.PUBLISHED) {
            // case A
            this.publish(content);
        }
    }

    public void updateFromPublished(PostStatus nextStatus, String content) {
        if (nextStatus == PostStatus.PUBLISHED) {
            // case B
            if (this.content != null && this.content.equals(content)) {
                return;
            }
            this.content = content;
            this.isRevised = true;
        } else if (nextStatus == PostStatus.DRAFT) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    public void publish(String content) {
        this.content = content;
        this.status = PostStatus.PUBLISHED;
    }


}
