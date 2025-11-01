package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import swyp_11.ssubom.domain.topic.entity.Category;

import java.util.List;

@Data
@AllArgsConstructor
public class TopicListResponse {
    private List<CategorySummaryDto> categories;
    private String selectedcategoryName;
    private List<TopicCollectionResponse> topics;
}
