package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swyp_11.ssubom.domain.post.dto.MyPostRequestDto;
import swyp_11.ssubom.domain.post.dto.MyReactedPostRequestDto;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.Reaction;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    Page<Post> findMyPosts(Long userId, MyPostRequestDto request, Pageable pageable);

    Page<Reaction> findMyReactedPosts(Long userId, MyReactedPostRequestDto request, Pageable pageable);

    List<Post> findPostsForInfiniteScroll(Long categoryId, LocalDateTime cursorUpdatedAt, Long cursorPostId, int limit);
}
