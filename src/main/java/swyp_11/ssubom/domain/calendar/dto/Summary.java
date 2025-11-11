package swyp_11.ssubom.domain.calendar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Summary {
    private Long totalWritingCount;
    private Long totalWeeklyChallengeCount;

    @Builder
    public Summary(Long totalWritingCount, Long totalWeeklyChallengeCount) {
        this.totalWritingCount = totalWritingCount;
        this.totalWeeklyChallengeCount = totalWeeklyChallengeCount;
    }

    public static Summary of(Long totalWritingCount, Long totalWeeklyChallengeCount) {
        return Summary.builder()
                .totalWritingCount(totalWritingCount)
                .totalWeeklyChallengeCount(totalWeeklyChallengeCount)
                .build();
    }
}
