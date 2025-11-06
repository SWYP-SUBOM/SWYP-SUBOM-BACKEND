package swyp_11.ssubom.domain.post.service;

import swyp_11.ssubom.domain.post.dto.ReactionResponse;
import swyp_11.ssubom.domain.post.dto.ReactionUpsertRequest;

public interface ReactionService {
    ReactionResponse upsertReaction(Long userId, Long postId, ReactionUpsertRequest request);

    ReactionResponse deleteReaction(Long userId, Long postId);
}

