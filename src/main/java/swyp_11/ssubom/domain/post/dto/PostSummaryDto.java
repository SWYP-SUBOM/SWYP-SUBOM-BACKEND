package swyp_11.ssubom.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.AIFeedback;
import swyp_11.ssubom.domain.post.entity.Post;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostSummaryDto {
    private Long postId;
    private String nickname;
    private String summary;
    private LocalDateTime updatedAt;
    private Long totalReactions;
    private Long postViews;

    public static PostSummaryDto of(Post post, AIFeedback aiFeedback, Long totalReactions, Long postViews) {
        return PostSummaryDto.builder()
                .postId(post.getPostId())
                .nickname(post.getNickname())
                .summary(aiFeedback.getSummary())
                .updatedAt(post.getUpdatedAt())
                .totalReactions(totalReactions)
                .postViews(postViews)
                .build();
    }

}
