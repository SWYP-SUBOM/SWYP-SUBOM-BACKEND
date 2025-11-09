package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.post.entity.AIFeedback;
import swyp_11.ssubom.domain.post.entity.Post;

import java.util.Optional;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AIFeedback, Long> {
    Optional<AIFeedback> findByPost_PostId(Long postId);
}
