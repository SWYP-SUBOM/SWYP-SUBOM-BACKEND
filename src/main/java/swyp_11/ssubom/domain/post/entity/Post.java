package swyp_11.ssubom.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;
import swyp_11.ssubom.domain.common.BaseTimeEntity;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post")
public class Post extends BaseTimeEntity {
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

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PostStatus status; //DRAFT, PUBLISHED

    @Column(name = "is_revised", nullable = false, columnDefinition = "boolean default false")
    private boolean isRevised;

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

    public void update(PostStatus nextStatus, String content) {
        if (this.status == PostStatus.DRAFT) {
            updateFromDraft(nextStatus, content);
        } else if (this.status == PostStatus.PUBLISHED) {
            updateFromPublished(nextStatus, content);
        }
    }

    public void updateFromDraft(PostStatus nextStatus, String content) {
        if (nextStatus == PostStatus.DRAFT) {
            if (this.content != null && this.content.equals(content)) {
                return;
            }
            this.content = content;
        } else if (nextStatus == PostStatus.PUBLISHED) {
            this.publish(content);
        }
    }

    public void updateFromPublished(PostStatus nextStatus, String content) {
        if (nextStatus == PostStatus.PUBLISHED) {
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

    public boolean isWrittenBy(Long userId) {
        return this.user != null && this.user.isSame(userId);
    }


}
