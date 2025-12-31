package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.entity.TopicType;

import java.util.List;


@Getter
@Builder
public class AdminTopicListResponse {
    private long totalCount;
    private List<AdminTopicDto> topics;

    public static AdminTopicListResponse of(List<AdminTopicDto> topics) {
        return AdminTopicListResponse.builder()
                .totalCount(topics.size())
                .topics(topics)
                .build();
    }

}