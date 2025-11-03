package swyp_11.ssubom.domain.writing.entity;

import jakarta.persistence.*;
import swyp_11.ssubom.domain.user.entity.User;

@Entity
@Table(
        name = "FeedReaction",
        uniqueConstraints = @UniqueConstraint(name = "uq_reaction_post_user", columnNames = {"post_id", "user_id"})
)
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_reactions_id")
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
}
