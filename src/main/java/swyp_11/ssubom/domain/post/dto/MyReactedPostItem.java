package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyReactedPostItem {
    private Long postId;
    private TopicInfo topicInfo;
    private String summary;
    private ReactionInfo reactionInfo;
    private String status;
    private boolean isRevised;
    private LocalDateTime updatedAt;
}
