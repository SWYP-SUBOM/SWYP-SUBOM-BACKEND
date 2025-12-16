package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.entity.TopicType;

import java.util.List;

@Builder
@Getter
public class AdminTopicListResponse {
    private long totalCount;
    private List<AdminTopicResponse> topics;

    public static AdminTopicListResponse of(List<AdminTopicResponse> topics) {
        return AdminTopicListResponse.builder()
                .totalCount(topics.size())
                .topics(topics)
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AdminTopicResponse {
        private Long categoryId;
        private String categoryName;
        private String topicName;
        private Long topicId;
        private TopicType topicType;

        public static AdminTopicResponse from(Topic topic) {
            return AdminTopicResponse.builder()
                    .categoryId(topic.getCategory().getId())
                    .categoryName(topic.getCategory().getName())
                    .topicName(topic.getName())
                    .topicId(topic.getId())
                    .topicType(topic.getTopicType())
                    .build();
        }

    }
}