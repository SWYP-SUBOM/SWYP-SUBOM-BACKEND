package swyp_11.ssubom.viewing.entity;

import jakarta.persistence.*;
import swyp_11.ssubom.user.entity.User;
import swyp_11.ssubom.writing.entity.Post;

@Entity
@Table(name = "PostView")
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User viewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

}
