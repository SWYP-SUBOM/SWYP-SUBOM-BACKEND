package swyp_11.ssubom.domain.topic.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TodayTopicListResponseDto {
    private List<TodayTopicResponseDto> topics;


    public static TodayTopicListResponseDto from(List<TodayTopicResponseDto> topics){
        return TodayTopicListResponseDto.builder()
                .topics(topics)
                .build();
    }
}
