package swyp_11.ssubom.domain.post.service;


import swyp_11.ssubom.domain.post.dto.*;

public interface PostService {
    PostCreateResponse createWriting(Long userId, PostCreateRequest request);

    PostUpdateResponse updateWriting(Long userId, Long postId, PostUpdateRequest request);

    void deleteWriting(Long userId, Long postId);

    TodayPostResponse findPostStatusByToday(Long userId);
}