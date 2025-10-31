package swyp_11.ssubom.domain.topic.dto;
import lombok.Data;
import swyp_11.ssubom.domain.topic.entity.Topic;

import java.time.LocalDate;

@Data
public class TopicCollectionResponse {
    String topicName;
    LocalDate usedAt;

    public static TopicCollectionResponse from(Topic topic) {
        TopicCollectionResponse dto = new TopicCollectionResponse();
        dto.setTopicName(topic.getName());
        dto.setUsedAt(topic.getUsedAt());
        return dto;
    }
}
