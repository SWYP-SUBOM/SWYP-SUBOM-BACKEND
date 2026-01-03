package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ReactionMetricsDto {
    private Long totalReactions;
    private Map<String, Long> countsByType;
}
