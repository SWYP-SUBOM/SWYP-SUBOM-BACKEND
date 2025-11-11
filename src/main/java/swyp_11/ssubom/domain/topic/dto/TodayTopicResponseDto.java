package swyp_11.ssubom.domain.topic.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Topic;

@Getter
@Builder
public class TodayTopicResponseDto {
    String categoryName;
    String topicName;
    Long categoryId;
    Long topicId;

    public static TodayTopicResponseDto of(String categoryName, String topicName,Long categoryId, Long topicId) {
        return TodayTopicResponseDto.builder()
                .categoryName(categoryName)
                .topicName(topicName)
                .categoryId(categoryId)
                .topicId(topicId)
                .build();
    }
}
