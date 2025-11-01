package swyp_11.ssubom.writing.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.writing.dto.*;
import swyp_11.ssubom.writing.repository.PostRepository;

public interface WritingService {
    WritingCreateResponse createWriting(Long userId, WritingCreateRequest request);

    WritingUpdateResponse updateWriting(Long userId, Long postId, WritingUpdateRequest request);

    void deleteWriting(Long userId, Long postId);

    ReactionResponse upsertReaction(Long userId, Long postId, ReactionUpsertRequest);

    ReactionResponse deleteReaction(Long userId, Long postId);
}