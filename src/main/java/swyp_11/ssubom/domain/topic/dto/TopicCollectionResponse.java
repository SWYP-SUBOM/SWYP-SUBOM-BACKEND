package swyp_11.ssubom.domain.topic.dto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Topic;

import java.time.LocalDate;

@Getter
@Builder
public class TopicCollectionResponse {
    Long topicId;
    String topicName;
    LocalDate usedAt;

    public static TopicCollectionResponse from(Topic topic) {
        return  TopicCollectionResponse.builder()
                .topicId(topic.getId())
                .topicName(topic.getName())
                .usedAt(topic.getUsedAt())
                .build();
    }
}
