package swyp_11.ssubom.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostDetailResponse {
    private String content;
    private LocalDateTime updatedAt;
    private WriterInfo writer;
    private List<PostReactionInfo> reactions;
    private MyReactionInfo myReaction;
    private Long viewCount;

    @Builder
    public PostDetailResponse(Post post, boolean isMe, List<PostReactionInfo> reactions, Long viewCount, MyReactionInfo myReaction) {
        this.content = post.getContent();
        this.updatedAt = post.getUpdatedAt();
        this.writer = new WriterInfo(post.getNickname(), isMe);
        this.reactions = reactions;
        this.viewCount = viewCount;
        this.myReaction = myReaction;
    }

    public static PostDetailResponse of(Post post, boolean isMe, List<PostReactionInfo> reactions, Long viewCount, MyReactionInfo myReaction) {
        return PostDetailResponse.builder()
                .post(post)
                .isMe(isMe)
                .reactions(reactions)
                .viewCount(viewCount)
                .myReaction(myReaction)
                .build();
    }
}
