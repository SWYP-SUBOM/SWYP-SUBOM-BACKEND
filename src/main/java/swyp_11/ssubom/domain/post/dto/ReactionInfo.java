package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionInfo {
    private String currentUserReaction;
    private ReactionMetricsDto metrics;
}
