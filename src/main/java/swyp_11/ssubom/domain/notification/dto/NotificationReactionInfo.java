package swyp_11.ssubom.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationReactionInfo {
    private Long reactionId;
    private String reactionName;
    private Long reactionCount;

    @Builder
    public NotificationReactionInfo(Long id, String name, long reactionCount) {
        this.reactionId = id;
        this.reactionName = name;
        this.reactionCount = reactionCount;
    }

    public static NotificationReactionInfo of(Long id, String name, long reactionCount) {
        return NotificationReactionInfo.builder()
                .id(id)
                .name(name)
                .reactionCount(reactionCount)
                .build();
    }
}