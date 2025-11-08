package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.PostStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Optional<Post> findFirstByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    boolean existsByNickname(String nickname);
    boolean existsByUser_UserIdAndStatusAndUpdatedAtBetween(Long userId, PostStatus postStatus, LocalDateTime startOfDay, LocalDateTime endOfDay);
    long countByUser_UserIdAndStatusAndUpdatedAtBetween(Long userId, PostStatus postStatus, LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
