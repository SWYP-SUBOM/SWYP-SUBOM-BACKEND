package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TopicListResponse {
    private String categoryName;
    private List<TopicCollectionResponse> topics;
}
