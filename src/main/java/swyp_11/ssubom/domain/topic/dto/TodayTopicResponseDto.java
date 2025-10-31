package swyp_11.ssubom.domain.topic.dto;

import lombok.Builder;
import lombok.Data;
import swyp_11.ssubom.domain.topic.entity.Topic;

@Data
@Builder
public class TodayTopicResponseDto {
    String categoryName;
    String topicName;

    public static TodayTopicResponseDto of(String categoryName, String topicName) {
        return TodayTopicResponseDto.builder()
                .categoryName(categoryName)
                .topicName(topicName)
                .build();
    }
}
