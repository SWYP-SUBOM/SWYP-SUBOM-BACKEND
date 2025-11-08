package swyp_11.ssubom.domain.post.dto;

import lombok.Getter;

@Getter
public class ReactionInfo {
    private String currentUserReaction;
    private ReactionMetricsDto metrics;
}
