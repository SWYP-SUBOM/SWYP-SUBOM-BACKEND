package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.PostView;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, Long> {
    Long countByPost(Post post);
}
