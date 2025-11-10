package swyp_11.ssubom.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ImprovementPointDto {
    private Long improvementPointId;
    private String content;

    @Builder
    public ImprovementPointDto(Long improvementPointId, String content) {
        this.improvementPointId = improvementPointId;
        this.content = content;
    }
}
