package swyp_11.ssubom.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.user.entity.User;

import java.time.LocalDate;

@Getter
public class UserProfileResponse {
    private String name;
    private String email;
    private LocalDate date;
    private StreakResponse streak;

    @Builder
    public UserProfileResponse(String name, String email, LocalDate date, StreakResponse streak) {
        this.name = name;
        this.email = email;
        this.date = date;
        this.streak = streak;
    }

    public static UserProfileResponse of(User user, StreakResponse StringResponse) {
        return UserProfileResponse.builder()
                .name(user.getUserName())
                .email(user.getEmail())
                .date(user.getCreatedAt().toLocalDate())
                .streak(StringResponse)
                .build();
    }
}