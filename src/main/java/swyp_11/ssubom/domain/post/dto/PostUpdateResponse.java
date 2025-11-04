package swyp_11.ssubom.domain.post.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.PostStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
public class PostUpdateResponse {
    private Long postId;

    private PostStatus status;

    private LocalDateTime updatedAt;

    @Lob
    private String content;

    public static PostUpdateResponse of(Post post) {
        return new PostUpdateResponse(
            post.getPostId(),
            post.getStatus(),
            post.getUpdatedAt(),
            post.getContent()
        );
    }
}
