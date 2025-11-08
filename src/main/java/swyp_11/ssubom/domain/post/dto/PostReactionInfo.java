package swyp_11.ssubom.domain.post.dto;

import lombok.Getter;

@Getter
public class PostReactionInfo {
    private Long reactionId;
    private String reactionName;
    private Long reactionCount;

    public PostReactionInfo(Long id, String name, long size) {
        this.reactionId = id;
        this.reactionName = name;
        this.reactionCount = size;
    }
}
