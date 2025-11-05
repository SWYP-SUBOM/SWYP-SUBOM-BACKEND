package swyp_11.ssubom.domain.topic.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.user.dto.StreakResponse;
import swyp_11.ssubom.domain.post.dto.TodayPostResponse;

import java.util.List;

@Getter
public class HomeResponse {
    private StreakResponse streak;
    private List<CategoryResponse> categories;
    private TodayPostResponse todayPost;

    @Builder
    public HomeResponse(StreakResponse streak, List<CategoryResponse> categories, TodayPostResponse todayPost) {
        this.streak = streak;
        this.categories = categories;
        this.todayPost = todayPost;
    }

    public static HomeResponse toDto(StreakResponse streakCount, List<CategoryResponse> categories, TodayPostResponse todayPosts) {
        return HomeResponse.builder()
                .streak(streakCount)
                .categories(categories)
                .todayPost(todayPosts)
                .build();
    }
}
