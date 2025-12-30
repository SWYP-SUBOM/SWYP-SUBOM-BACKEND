package swyp_11.ssubom.domain.notification.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.notification.entity.Notification;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.ReactionType;
import swyp_11.ssubom.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByReceiver_UserIdAndIsReadFalse(Long userId);

    Optional<Notification> findByReceiverAndPostAndReactionType(User receiver, Post post, ReactionType type);

    @Query("""
            SELECT n FROM Notification n
            WHERE n.receiver.userId = :userId
            AND n.updatedAt < :cursor
            ORDER BY n.updatedAt DESC
            """)
    List<Notification> findRecentNotifications(
            @Param("userId") Long userId,
            @Param("cursor") LocalDateTime cursor,
            Pageable pageable
    );
}
