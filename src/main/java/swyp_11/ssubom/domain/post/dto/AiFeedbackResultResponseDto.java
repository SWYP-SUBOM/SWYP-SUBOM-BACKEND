package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import swyp_11.ssubom.domain.post.entity.AIFeedbackStatus;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class AiFeedbackResultResponseDto {
    private Long aiFeedbackId;
    private AIFeedbackStatus status;
    private String strength;
    private String summary;
    private List<String> improvementPoints;

    @Builder
    public AiFeedbackResultResponseDto(Long aiFeedbackId, AIFeedbackStatus status) {
        this.status = status;
        this.aiFeedbackId = aiFeedbackId;
        this.strength = "";
        this.summary = "";
        this.improvementPoints = new ArrayList<>();
    }
}
