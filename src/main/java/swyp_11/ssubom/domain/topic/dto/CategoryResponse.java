package swyp_11.ssubom.domain.topic.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;

    @Builder
    public CategoryResponse(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public static CategoryResponse toDto(Long categoryId, String categoryName) {
        return CategoryResponse.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .build();
    }
}
