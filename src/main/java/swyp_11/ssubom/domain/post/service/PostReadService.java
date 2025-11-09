package swyp_11.ssubom.domain.post.service;

import swyp_11.ssubom.domain.post.dto.*;

public interface PostReadService {
    MyPostResponseDto getMyPosts(Long userId, MyPostRequestDto request);
    MyReactedPostResponseDto getMyReactedPost(Long userId, MyReactedPostRequestDto request);
    MyPostDetailResponseDto getPostDetail(Long postId);
}
