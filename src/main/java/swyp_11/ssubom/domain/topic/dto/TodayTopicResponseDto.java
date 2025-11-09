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

    public static TodayTopicResponseDto of(String categoryName, String topicName) {
        return TodayTopicResponseDto.builder()
                .categoryName(categoryName)
                .topicName(topicName)
                .build();
    }
}
