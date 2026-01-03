package swyp_11.ssubom.domain.topic.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CategorySummaryDto {
    private Long categoryId;
    private String categoryName;

}
