package swyp_11.ssubom.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.ReactionType;

@Getter
public class MyReactionInfo {
    private Long reactionId;
    private String reactionName;

    @Builder
    public MyReactionInfo(Long reactionId, String reactionName) {
        this.reactionId = reactionId;
        this.reactionName = reactionName;
    }

    public static MyReactionInfo of(ReactionType reactionType) {
        if (reactionType == null) return null;
        return MyReactionInfo.builder()
                .reactionId(reactionType.getId())
                .reactionName(reactionType.getName())
                .build();
    }
}
