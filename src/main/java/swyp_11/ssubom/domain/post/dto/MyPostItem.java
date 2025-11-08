package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import swyp_11.ssubom.domain.post.entity.PostStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
public class MyPostItem {
    private Long postId;
    private TopicInfo topicInfo;
    private String summary;
    private String status;
    private boolean isRevised;
    private LocalDateTime updatedAt;
}
