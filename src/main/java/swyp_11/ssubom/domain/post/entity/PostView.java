package swyp_11.ssubom.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;
import swyp_11.ssubom.domain.user.entity.User;

@Getter
@Entity
@Table(name = "post_view")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostView extends BaseTimeEntity {

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
