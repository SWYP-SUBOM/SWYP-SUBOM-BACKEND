package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.PostStatus;
import swyp_11.ssubom.domain.topic.entity.Topic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Optional<Post> findFirstByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    boolean existsByNickname(String nickname);
    boolean existsByUser_UserIdAndStatusAndUpdatedAtBetween(Long userId, PostStatus postStatus, LocalDateTime startOfDay, LocalDateTime endOfDay);
    long countByUser_UserIdAndStatusAndUpdatedAtBetween(Long userId, PostStatus postStatus, LocalDateTime localDateTime, LocalDateTime localDateTime1);
    List<Post> findByUser_UserIdAndStatusAndCreatedAtBetween(Long userId, PostStatus postStatus, LocalDateTime localDateTime, LocalDateTime localDateTime1);
    List<Post> findByTopicAndStatusOrderByUpdatedAtDesc(Topic topic, PostStatus status);
    long countByUser_UserIdAndStatusAndCreatedAtBetween(Long userId, PostStatus postStatus, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    @Query("""
    SELECT p.postId
    FROM Post p
    LEFT JOIN p.reactions r
    LEFT JOIN p.postViews pv
    WHERE p.status = 'PUBLISHED'
      AND p.createdAt >= :startOfDay
      AND p.createdAt < :endOfDay
    GROUP BY p.postId
    ORDER BY (COUNT(DISTINCT r.id) + COUNT(DISTINCT pv.id)) DESC,
        p.updatedAt ASC
    LIMIT 1
    """)
    Optional<Long> findTodayPopularPostId(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
