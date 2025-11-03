package swyp_11.ssubom.writing.dto;

import lombok.Getter;
import swyp_11.ssubom.domain.entity.Post;

@Getter
public class WritingCreateResponse {

    private Long postId;
    private String nickname;

    private WritingCreateResponse(Long postId, String nickname) {
        this.postId = postId;
        this.nickname = nickname;
    }

    public static WritingCreateResponse of(Post post) {
        return new WritingCreateResponse(post.getPostId(), post.getNickname());
    }

}
