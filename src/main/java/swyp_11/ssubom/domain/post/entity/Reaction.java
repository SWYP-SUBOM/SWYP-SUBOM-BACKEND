package swyp_11.ssubom.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;
import swyp_11.ssubom.domain.common.BaseTimeEntity;
import swyp_11.ssubom.domain.user.entity.User;


@Entity
@Table(
        name = "feed_reaction",
        uniqueConstraints = @UniqueConstraint(name = "uq_reaction_post_user", columnNames = {"post_id", "user_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_reaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reaction_type_id", nullable = false)
    private ReactionType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder(access = AccessLevel.PRIVATE)
    public Reaction(Post post, ReactionType type, User user) {
        this.post = post;
        this.type = type;
        this.user = user;
    }

    public static Reaction create(User user, Post post, ReactionType type) {
        if (user == null || post == null || type == null) {
            throw new IllegalArgumentException("User,Post, ReactionType은 필수입니다.");
        }
        return Reaction.builder()
                .user(user)
                .post(post)
                .type(type)
                .build();
    }

    public void addType(ReactionType reactionType) {
        if (reactionType == null) {
            return;
        }
        if (reactionType.equals(this.type)) {
            return;
        }
        this.type = reactionType;
    }

}

