package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyPostDetailResponseDto {
    private Long postId;
    private Long topicId;
    private String nickname;
    private TopicInfo topicInfo;
    private String content;
    private String status;
    private LocalDateTime updatedAt;
    private boolean isRevised;
    private AiFeedbackInfo aiFeedbackInfo;
}
