package swyp_11.ssubom.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.PostStatus;

@Getter
public class TodayPostResponse {
    private Long postId;
    private String postStatus;

    @Builder
    public TodayPostResponse(Long postId, String postStatus) {
        this.postId = postId;
        this.postStatus = postStatus;
    }

    public static TodayPostResponse toDto(Long postId, PostStatus status) {
        return TodayPostResponse.builder()
                .postId(postId)
                .postStatus(status.name())
                .build();
    }
}
