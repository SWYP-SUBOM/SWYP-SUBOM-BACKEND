package swyp_11.ssubom.domain.topic.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Category;

@Getter
@AllArgsConstructor
@Builder
public class CategorySummaryDto {
    private Long categoryId;
    private String categoryName;

    public static CategorySummaryDto of(Category category) {
        return CategorySummaryDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }
}
