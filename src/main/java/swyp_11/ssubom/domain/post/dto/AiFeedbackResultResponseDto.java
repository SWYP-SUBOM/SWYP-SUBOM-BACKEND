package swyp_11.ssubom.domain.post.dto;

import lombok.*;
import swyp_11.ssubom.domain.post.entity.AIFeedbackStatus;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class AiFeedbackResultResponseDto {
    private Long aiFeedbackId;
    private AIFeedbackStatus status;
    private String strength;
    private String summary;
    private List<FeedbackPointDto> improvementPoints;
    private String errorMessage;
    private String grade;

    @Builder
    public AiFeedbackResultResponseDto(Long aiFeedbackId, AIFeedbackStatus status,
                                       String strength, String summary,
                                       List<FeedbackPointDto> improvementPoints,
                                       String errorMessage, String grade) {
        this.aiFeedbackId = aiFeedbackId;
        this.status = status;
        this.strength = strength;
        this.summary = summary;
        this.improvementPoints = improvementPoints != null ? improvementPoints : new ArrayList<>();
        this.errorMessage = errorMessage;
        this.grade = grade;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedbackPointDto {
        private String reason;        // 피드백 내용
        private int sentenceIndex;    // 문장 번호
        private String originalText;  // 원본 문장 (프론트 하이라이팅용)
    }
}
