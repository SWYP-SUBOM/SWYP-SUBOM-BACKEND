package swyp_11.ssubom.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StreakResponse {
    private Long current;

    @Builder
    public StreakResponse(Long current) {
        this.current = current;
    }

    public static StreakResponse toDto(Long streakCount) {
        return StreakResponse.builder()
                .current(streakCount)
                .build();
    }
}
