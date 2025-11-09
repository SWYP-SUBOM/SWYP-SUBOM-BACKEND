package swyp_11.ssubom.domain.calendar.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Category;

@Getter
@Builder
public class CategoryInfo {
    private Long categoryId;
    private String categoryName;

    @Builder
    public CategoryInfo(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public static CategoryInfo of(Category category) {
        return CategoryInfo.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }
}