package swyp_11.ssubom.domain.post.dto;

import java.time.LocalDateTime;

public class MyPostDetailResponseDto {
    private Long postId;
    private String nickname;
    private TopicInfo topicInfo;
    private String content;
    private String status;
    private LocalDateTime updatedAt;
    private boolean isRevised;
    private AiFeedbackInfo aiFeedbackInfo;
}
