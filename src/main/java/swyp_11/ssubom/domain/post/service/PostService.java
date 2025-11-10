package swyp_11.ssubom.domain.post.service;


import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {
    PostCreateResponse createPost(Long userId, PostCreateRequest request);

    PostUpdateResponse updatePost(Long userId, Long postId, PostUpdateRequest request);

    void deletePost(Long userId, Long postId);

    TodayPostResponse findPostStatusByToday(Long userId);

    PostDetailResponse getPostDetail(CustomOAuth2User user, Long postId);

    PostListResponseDto getPostList(Long categoryId,LocalDateTime cursorUpdatedAt,Long cursorPostId);

   AiFeedbackResponse getAiFeedback(Long userId, Long postId , Long AiFeedbackId);


}