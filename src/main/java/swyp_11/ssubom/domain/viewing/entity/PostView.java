package swyp_11.ssubom.domain.viewing.entity;

import jakarta.persistence.*;
import swyp_11.ssubom.domain.writing.entity.Post;
import swyp_11.ssubom.global.security.entity.User;

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
