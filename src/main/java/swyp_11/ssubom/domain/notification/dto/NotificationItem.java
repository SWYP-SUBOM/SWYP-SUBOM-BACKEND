package swyp_11.ssubom.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.calendar.dto.CategoryInfo;
import swyp_11.ssubom.domain.notification.entity.Notification;

import java.time.LocalDateTime;

@Getter
public class NotificationItem {
    private Long notificationId;
    private String reactionName;
    private Long reactionCount;
    private LocalDateTime updatedAt;
    private CategoryInfo category;
    private Long postId;
    private boolean isRead;

    @Builder
    public NotificationItem(Long notificationId, String reactionName, Long reactionCount, LocalDateTime updatedAt, CategoryInfo category, Long postId, boolean isRead) {
        this.notificationId = notificationId;
        this.reactionName = reactionName;
        this.reactionCount = reactionCount;
        this.updatedAt = updatedAt;
        this.category = category;
        this.postId = postId;
        this.isRead = isRead;
    }

    public static NotificationItem of(Notification notification, CategoryInfo category) {
        return NotificationItem.builder()
                .notificationId(notification.getId())
                .reactionName(notification.getReactionType().getName())
                .reactionCount(notification.getActorCount())
                .updatedAt(notification.getUpdatedAt())
                .category(category)
                .postId(notification.getPost().getPostId())
                .isRead(notification.isRead())
                .build();
    }
}
