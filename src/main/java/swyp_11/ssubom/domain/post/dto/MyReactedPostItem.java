package swyp_11.ssubom.domain.post.dto;

import java.time.LocalDateTime;

public class MyReactedPostItem {
    private Long postId;
    private TopicInfo topicInfo;
    private String summary;
    private ReactionInfo reactionInfo;
    private String status;
    private boolean isRevised;
    private LocalDateTime updatedAt;
}
