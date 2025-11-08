package swyp_11.ssubom.domain.post.service;


import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;

public interface PostService {
    PostCreateResponse createPost(Long userId, PostCreateRequest request);

    PostUpdateResponse updatePost(Long userId, Long postId, PostUpdateRequest request);

    void deletePost(Long userId, Long postId);

    TodayPostResponse findPostStatusByToday(Long userId);

    PostDetailResponse getPostDetail(CustomOAuth2User user, Long postId);
}