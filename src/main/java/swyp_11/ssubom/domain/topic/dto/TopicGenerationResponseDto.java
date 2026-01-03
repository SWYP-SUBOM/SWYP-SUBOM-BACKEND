package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.TopicGeneration;
import swyp_11.ssubom.domain.topic.entity.TopicGenerationStatus;

@Getter
@AllArgsConstructor
public class TopicGenerationResponseDto {
    private Long generationId;
    private TopicGenerationStatus status;
    private String errorMessage;

    public static TopicGenerationResponseDto from(TopicGeneration tg) {
        return new TopicGenerationResponseDto(
                tg.getId(),
                tg.getStatus(),
                tg.getErrorMessage()
        );
    }
}
