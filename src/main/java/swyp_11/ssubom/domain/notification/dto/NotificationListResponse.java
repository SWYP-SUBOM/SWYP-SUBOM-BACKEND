package swyp_11.ssubom.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NotificationListResponse {
    private List<NotificationItem> notifications;
    private Long nextBeforeId;
    private boolean hasMore;

    @Builder
    public NotificationListResponse(List<NotificationItem> notifications, Long nextBeforeId, boolean hasMore) {
        this.notifications = notifications;
        this.nextBeforeId = nextBeforeId;
        this.hasMore = hasMore;
    }

    public static NotificationListResponse of(List<NotificationItem> notifications, Long nextBeforeId, boolean hasMore) {
        return NotificationListResponse.builder()
                .notifications(notifications)
                .nextBeforeId(nextBeforeId)
                .hasMore(hasMore)
                .build();
    }
}
