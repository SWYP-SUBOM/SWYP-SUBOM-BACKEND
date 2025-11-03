package swyp_11.ssubom.writing.service;


import swyp_11.ssubom.writing.dto.*;

public interface WritingService {
    WritingCreateResponse createWriting(Long userId, WritingCreateRequest request);

    WritingUpdateResponse updateWriting(Long userId, Long postId, WritingUpdateRequest request);

    void deleteWriting(Long userId, Long postId);

}