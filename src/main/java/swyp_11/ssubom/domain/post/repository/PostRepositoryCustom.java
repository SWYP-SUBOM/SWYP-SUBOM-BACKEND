package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import swyp_11.ssubom.domain.post.dto.MyPostRequestDto;
import swyp_11.ssubom.domain.post.dto.MyReactedPostRequestDto;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.Reaction;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    Slice<Post> findMyPosts(Long userId, MyPostRequestDto request, Pageable pageable);

    Slice<Reaction> findMyReactedPosts(Long userId, MyReactedPostRequestDto request, Pageable pageable);
    List<Post> findPostsForInfiniteScroll(Long topicId, LocalDateTime cursorUpdatedAt, Long cursorPostId, int limit);
}
