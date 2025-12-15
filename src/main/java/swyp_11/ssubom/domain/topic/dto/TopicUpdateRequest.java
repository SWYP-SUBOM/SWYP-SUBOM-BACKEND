package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.TopicType;

@AllArgsConstructor
@Getter
public class TopicUpdateRequest { //
    String topicName;
    TopicType topicType;
    Long categoryId; // (선택적)
}