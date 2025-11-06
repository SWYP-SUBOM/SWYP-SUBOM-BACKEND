package swyp_11.ssubom.domain.post.dto;

import jakarta.persistence.Lob;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.PostStatus;;

@Getter
public class PostUpdateRequest {

    private String content;

    private PostStatus status;
}
