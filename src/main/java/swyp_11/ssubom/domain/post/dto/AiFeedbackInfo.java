package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AiFeedbackInfo {
    private Long aiFeedbackId;

    private String strengthPoint;

    private List<String> improvementPoints;
}
