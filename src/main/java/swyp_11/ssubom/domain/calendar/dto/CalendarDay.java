package swyp_11.ssubom.domain.calendar.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.Post;

import java.time.LocalDate;

@Getter
@Builder
public class CalendarDay {
    private LocalDate date;
    private String dayOfWeek;
    private boolean hasWriting;
    private Long postId;
    private CategoryInfo category;

    @Builder
    public CalendarDay(LocalDate date, String dayOfWeek, boolean hasWriting, Long postId, CategoryInfo category) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.hasWriting = hasWriting;
        this.postId = postId;
        this.category = category;
    }

    public static CalendarDay of(LocalDate date, Post post) {
        if (post == null) {
            return CalendarDay.builder()
                    .date(date)
                    .dayOfWeek(date.getDayOfWeek().name())
                    .hasWriting(false)
                    .build();
        }
        return CalendarDay.builder()
                .date(date)
                .dayOfWeek(date.getDayOfWeek().name())
                .hasWriting(true)
                .postId(post.getPostId())
                .category(CategoryInfo.of(post.getTopic().getCategory()))
                .build();
    }
}
