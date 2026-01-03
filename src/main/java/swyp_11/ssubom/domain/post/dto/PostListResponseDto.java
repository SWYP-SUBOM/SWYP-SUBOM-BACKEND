package swyp_11.ssubom.domain.post.dto;


import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.topic.entity.Topic;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostListResponseDto {
    private String topicName;
    private String categoryName;
    private List<PostSummaryDto> postList;
    private LocalDateTime curUpdatedAt;
    private Long curPostId ;
    private boolean hasMore;

    @Builder
    public PostListResponseDto(Topic topic, List<PostSummaryDto> postList,LocalDateTime curUpdatedAt,Long curPostId  ,boolean hasMore) {
        this.topicName = topic.getName();
        this.categoryName = topic.getCategory().getName();
        this.postList = postList;
        this.curUpdatedAt = curUpdatedAt;
        this.curPostId =curPostId ;
        this.hasMore=hasMore;
    }

    public static PostListResponseDto from(Topic topic, List<PostSummaryDto> postList,LocalDateTime curUpdatedAt,Long curPostId ,boolean hasMore) {
        return PostListResponseDto.builder()
                .topic(topic)
                .postList(postList)
                .curPostId (curPostId)
                .curUpdatedAt(curUpdatedAt)
                .hasMore(hasMore)
                .build();
    }
}
