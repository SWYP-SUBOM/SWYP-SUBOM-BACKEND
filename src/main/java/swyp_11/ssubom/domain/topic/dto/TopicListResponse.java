package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Category;

import java.util.List;

@Getter
@AllArgsConstructor
public class TopicListResponse {
    private List<CategorySummaryDto> categories;
    private String selectedCategoryName;
    private List<TopicCollectionResponse> topics;
}
