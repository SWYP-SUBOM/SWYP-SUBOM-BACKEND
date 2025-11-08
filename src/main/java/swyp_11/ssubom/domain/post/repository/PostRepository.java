package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.PostStatus;
import swyp_11.ssubom.domain.topic.entity.Topic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findFirstByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    boolean existsByNickname(String nickname);
    List<Post> findByTopicAndStatusOrderByUpdatedAtDesc(Topic topic, PostStatus status);
}
