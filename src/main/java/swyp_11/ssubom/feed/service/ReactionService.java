package swyp_11.ssubom.feed.service;

import swyp_11.ssubom.feed.dto.ReactionResponse;
import swyp_11.ssubom.feed.dto.ReactionUpsertRequest;

public interface ReactionService {
    ReactionResponse upsertReaction(Long userId, Long postId, ReactionUpsertRequest request);

    ReactionResponse deleteReaction(Long userId, Long postId);
}

