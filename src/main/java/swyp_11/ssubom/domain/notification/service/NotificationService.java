package swyp_11.ssubom.domain.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swyp_11.ssubom.domain.notification.dto.NotificationListResponse;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.ReactionType;
import swyp_11.ssubom.domain.user.entity.User;

import java.time.LocalDateTime;

public interface NotificationService {
    SseEmitter connect(Long userId);
    void createReactionNotification(Post post, User user, ReactionType oldType, ReactionType newType);
    NotificationListResponse getNotifications(Long userId, int limit, LocalDateTime beforeUpdatedAt);
}
