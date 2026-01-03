package swyp_11.ssubom.domain.post.dto;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HyperClovaResponseDto {
    private String summary;
    private String strength;
    private List<FeedbackPoint> improvementPoints;
    private String grade;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class FeedbackPoint {
        private String reason;
        // LLM이 주는 값 (몇 번째 문장인지)
        private int sentenceIndex;
        // 우리가 채워 넣을 값 (프론트 하이라이팅용 원본 텍스트)
        private String originalText;
    }
}
