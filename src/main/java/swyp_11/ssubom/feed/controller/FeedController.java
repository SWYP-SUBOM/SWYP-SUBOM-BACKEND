package swyp_11.ssubom.feed.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp_11.ssubom.feed.service.ReactionService;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.feed.dto.ReactionResponse;
import swyp_11.ssubom.feed.dto.ReactionUpsertRequest;


@Slf4j
@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final ReactionService reactionService;

    @PutMapping("/{feedId}/reaction")
    public ResponseEntity<ApiResponse<ReactionResponse>> upsertReaction(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody ReactionUpsertRequest request
    ) {
        if (userId == null) {
            // 임시 예외 처리 또는 테스트용 ID 할당
            log.warn("UserId is null, using test user ID 1L for development.");
            userId = 1L;
            // throw new BusinessException(ErrorCode.UNAUTHORIZED); // 실제 운영 시
        }

        ReactionResponse reactionResponse = reactionService.upsertReaction(userId, postId, request);

        ApiResponse<ReactionResponse> responseBody = ApiResponse.success(
                reactionResponse,
                "R0001",
                "반응 등록/수정에 성공했습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @DeleteMapping("/{feedId}/reaction")
    public ResponseEntity<ApiResponse<ReactionResponse>> deleteReaction(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        if (userId == null) {
            // 임시 예외 처리 또는 테스트용 ID 할당
            log.warn("UserId is null, using test user ID 1L for development.");
            userId = 1L;
            // throw new BusinessException(ErrorCode.UNAUTHORIZED); // 실제 운영 시
        }
        ReactionResponse reactionResponse = reactionService.deleteReaction(userId, postId);

        ApiResponse<ReactionResponse> responseBody = ApiResponse.success(
                reactionResponse,
                "R0002",
                "반응 삭제에 성공했습니다."
        );

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);

    }
}
