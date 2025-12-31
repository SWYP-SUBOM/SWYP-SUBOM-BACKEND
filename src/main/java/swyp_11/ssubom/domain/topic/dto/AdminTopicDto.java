package swyp_11.ssubom.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Status;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.entity.TopicType;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class AdminTopicDto {
    private Long categoryId;
    private String categoryName;
    private Long topicId;
    private String topicName;
    private TopicType topicType;
    private Status topicStatus;
    private LocalDate usedAt;

    public static AdminTopicDto from(Topic topic){
            return AdminTopicDto.builder()
                    .categoryId(topic.getCategory().getId())
                    .categoryName(topic.getCategory().getName())
                    .topicId(topic.getId())
                    .topicName(topic.getName())
                    .topicType(topic.getTopicType())
                    .topicStatus(topic.getTopicStatus())
                    .usedAt(topic.getUsedAt())
                    .build();
    }
}
