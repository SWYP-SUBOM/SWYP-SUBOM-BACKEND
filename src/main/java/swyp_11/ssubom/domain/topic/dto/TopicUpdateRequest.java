package swyp_11.ssubom.domain.topic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.TopicType;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class TopicUpdateRequest { //
    String topicName;
    TopicType topicType;
    Long categoryId;
}