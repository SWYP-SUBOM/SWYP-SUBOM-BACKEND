package swyp_11.ssubom.domain.post.dto;

import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.Post;

@Getter
public class PostCreateResponse {

    private Long postId;
    private String nickname;

    private PostCreateResponse(Long postId, String nickname) {
        this.postId = postId;
        this.nickname = nickname;
    }

    public static PostCreateResponse of(Post post) {
        return new PostCreateResponse(post.getPostId(), post.getNickname());
    }

}
