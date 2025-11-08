package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swyp_11.ssubom.domain.post.dto.MyPostRequestDto;
import swyp_11.ssubom.domain.post.entity.Post;

public interface PostRepositoryCustom {
    Page<Post> findMyPosts(Long userId, MyPostRequestDto request, Pageable pageable);
}
