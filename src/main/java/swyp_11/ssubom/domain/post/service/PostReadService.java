package swyp_11.ssubom.domain.post.service;

import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;

public interface PostReadService {
    MyPostResponseDto getMyPosts(Long userId, MyPostRequestDto request);
    MyReactedPostResponseDto getMyReactedPost(Long userId, MyReactedPostRequestDto request);
    MyPostDetailResponseDto getMyPostDetail(CustomOAuth2User user, Long postId);
}
