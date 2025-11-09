package swyp_11.ssubom.domain.calendar.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.user.entity.Streak;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CalendarResponse {
    private LocalDateTime currentDate;
    private Summary summary;
    private List<CalendarDay> days;
    private List<LocalDate> streakDates;

    @Builder
    public CalendarResponse(LocalDateTime currentDate, Summary summary, List<CalendarDay> days, List<LocalDate> streakDates) {
        this.currentDate = currentDate;
        this.summary = summary;
        this.days = days;
        this.streakDates = streakDates;
    }

    public static CalendarResponse of(LocalDate today, long totalPublishedCount, Streak streak,
                                      List<CalendarDay> days, List<LocalDate> streakDates) {
        return CalendarResponse.builder()
                .currentDate(today.atStartOfDay())
                .summary(Summary.of(totalPublishedCount, streak.getWeeklyChallengeCount()))
                .days(days)
                .streakDates(streakDates)
                .build();
    }
}
