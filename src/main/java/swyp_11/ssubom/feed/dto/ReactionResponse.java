package swyp_11.ssubom.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionResponse {
    private Long postId;
    private ReactionMetricsDto metrics;
    private String currentUserReaction;
}
