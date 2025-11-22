package swyp_11.ssubom.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NotificationListResponse {
    private List<NotificationItem> notifications;
    private LocalDateTime cursor;
    private boolean hasMore;

    @Builder
    public NotificationListResponse(List<NotificationItem> notifications, LocalDateTime cursor, boolean hasMore) {
        this.notifications = notifications;
        this.cursor = cursor;
        this.hasMore = hasMore;
    }

    public static NotificationListResponse of(List<NotificationItem> notifications, LocalDateTime nextCursor, boolean hasMore) {
        return NotificationListResponse.builder()
                .notifications(notifications)
                .cursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }
}
