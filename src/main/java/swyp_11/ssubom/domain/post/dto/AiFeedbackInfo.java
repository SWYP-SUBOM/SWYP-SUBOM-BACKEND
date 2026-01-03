package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.ImprovementPoint;

import java.util.List;

@Getter
@AllArgsConstructor
public class AiFeedbackInfo {
    private Long aiFeedbackId;

    private String strengthPoint;

    private List<ImprovementPoint> improvementPoints;
}
