package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swyp_11.ssubom.domain.post.dto.MyPostRequestDto;
import swyp_11.ssubom.domain.post.dto.MyReactedPostRequestDto;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.Reaction;

public interface PostRepositoryCustom {
    Page<Post> findMyPosts(Long userId, MyPostRequestDto request, Pageable pageable);

    Page<Reaction> findMyReactedPosts(Long userId, MyReactedPostRequestDto request, Pageable pageable);
}
