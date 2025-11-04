package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.post.entity.Post;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findFirstByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    boolean existsByNickname(String nickname);
}
