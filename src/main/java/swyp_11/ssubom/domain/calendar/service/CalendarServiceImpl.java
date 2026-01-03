package swyp_11.ssubom.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.calendar.dto.CalendarDay;
import swyp_11.ssubom.domain.calendar.dto.CalendarResponse;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.PostStatus;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.domain.user.entity.Streak;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.user.repository.StreakRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarServiceImpl implements CalendarService{

    private final PostRepository postRepository;
    private final StreakRepository streakRepository;

    @Override
    public CalendarResponse getCalendar(User user, int year, int month) {
        LocalDate today = LocalDate.now();

        LocalDate start = getMonthStart(year, month);
        LocalDate end = getMonthEnd(start);

        List<Post> posts = getUserPosts(user, start, end);
        Streak streak = getUserStreak(user);
        long totalPublishedCount = getMonthlyPostCount(user, start, end);

        List<CalendarDay> days = buildCalendarDays(start, end, posts);
        List<LocalDate> streakDates = getCurrentWeekStreakDates(today, posts);

        return CalendarResponse.of(today, totalPublishedCount, streak, days, streakDates);
    }

    private long getMonthlyPostCount(User user, LocalDate start, LocalDate end) {
        return postRepository.countByUser_UserIdAndStatusAndCreatedAtBetween(
                user.getUserId(),
                PostStatus.PUBLISHED,
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX)
        );
    }

    private static List<LocalDate> getCurrentWeekStreakDates(LocalDate today, List<Post> posts) {
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() % 7);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return posts.stream()
                .map(post -> post.getCreatedAt().toLocalDate())
                .filter(date -> !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek))
                .sorted()
                .toList();
    }

    private static List<CalendarDay> buildCalendarDays(LocalDate start, LocalDate end, List<Post> posts) {
        return start.datesUntil(end.plusDays(1))
                .map(date -> {
                    Post match = posts.stream()
                            .filter(p -> p.getCreatedAt().toLocalDate().equals(date))
                            .findFirst()
                            .orElse(null);
                    return CalendarDay.of(date, match);
                })
                .toList();
    }

    private Streak getUserStreak(User user) {
        return streakRepository.findByUser(user)
                .orElse(Streak.empty(user));
    }

    private List<Post> getUserPosts(User user, LocalDate start, LocalDate end) {
        return postRepository.findByUser_UserIdAndStatusAndCreatedAtBetween(
                user.getUserId(), PostStatus.PUBLISHED, start.atStartOfDay(), end.atTime(LocalTime.MAX)
        );
    }

    private static LocalDate getMonthEnd(LocalDate start) {
        return start.withDayOfMonth(start.lengthOfMonth());
    }

    private static LocalDate getMonthStart(int year, int month) {
        return LocalDate.of(year, month, 1);
    }
}
