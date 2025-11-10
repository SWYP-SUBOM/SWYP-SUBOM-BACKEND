package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.AIFeedbackStatus;

import java.util.List;

@Getter
@AllArgsConstructor
public class AiFeedbackStartResponseDto {
    private Long aiFeedbackId;
    private AIFeedbackStatus status;
}
