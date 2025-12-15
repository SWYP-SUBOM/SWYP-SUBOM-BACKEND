package swyp_11.ssubom.domain.topic.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.entity.TopicType;

@Getter
@Builder
public class TodayTopicResponseDto {
    String categoryName;
    String topicName;
    Long categoryId;
    Long topicId;
    TopicType topicType;

    public static TodayTopicResponseDto of(String categoryName, String topicName,Long categoryId, Long topicId, TopicType topicType) {
        return TodayTopicResponseDto.builder()
                .categoryName(categoryName)
                .topicName(topicName)
                .categoryId(categoryId)
                .topicId(topicId)
                .topicType(topicType)
                .build();
    }

    public static TodayTopicResponseDto fromTopic(Topic savedTopic) {
        return TodayTopicResponseDto.builder()
                .categoryName(savedTopic.getCategory().getName())
                .topicName(savedTopic.getName())
                .categoryId(savedTopic.getCategory().getId())
                .topicId(savedTopic.getId())
                .topicType(savedTopic.getTopicType())
                .build();
    }
}
