package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.TopicType;

@AllArgsConstructor
@Getter
public class TopicCreationRequest {
    String topicName;
    TopicType topicType;
}
