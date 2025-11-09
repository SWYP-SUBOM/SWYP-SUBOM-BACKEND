package swyp_11.ssubom.domain.post.dto;


import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Topic;
import java.util.List;

@Getter
public class PostListResponseDto {
    private String topicName;
    private String categoryName;
    private List<PostSummaryDto> postList;

    @Builder
    public PostListResponseDto(Topic topic, List<PostSummaryDto> postList) {
        this.topicName = topic.getName();
        this.categoryName = topic.getCategory().getName();
        this.postList = postList;
    }

    public static PostListResponseDto from(Topic topic, List<PostSummaryDto> postList) {
        return PostListResponseDto.builder()
                .topic(topic)
                .postList(postList)
                .build();
    }
}
