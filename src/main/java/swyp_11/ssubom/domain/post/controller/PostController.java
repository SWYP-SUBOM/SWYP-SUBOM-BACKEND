package swyp_11.ssubom.domain.post.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.post.service.ReactionService;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.domain.post.service.PostService;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>> createWriting(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        if (userId == null) {
            // 임시 예외 처리 또는 테스트용 ID 할당
            log.warn("UserId is null, using test user ID 1L for development.");
            userId = 1L;
            // throw new BusinessException(ErrorCode.UNAUTHORIZED); // 실제 운영 시
        }

        PostCreateResponse postCreateResponse = postService.createWriting(userId, request);
        ApiResponse<PostCreateResponse> responseBody = ApiResponse.success(
                postCreateResponse,
                "W0001",
                "글 임시저장에 성공했습니다"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/{writingId}")
    public ResponseEntity<ApiResponse<PostUpdateResponse>> updateWriting(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        if (userId == null) {
            // 임시 예외 처리 또는 테스트용 ID 할당
            log.warn("UserId is null, using test user ID 1L for development.");
            userId = 1L;
            // throw new BusinessException(ErrorCode.UNAUTHORIZED); // 실제 운영 시
        }

        PostUpdateResponse postUpdateResponse = postService.updateWriting(
                userId,
                postId,
                request
        );

        ApiResponse<PostUpdateResponse> responseBody = ApiResponse.success(
                postUpdateResponse,
                "W0002",
                "임시저장한 글 수정에 성공했습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @DeleteMapping("/{writingId}")
    public ResponseEntity<ApiResponse<Void>> deleteWriting(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        if (userId == null) {
            // 임시 예외 처리 또는 테스트용 ID 할당
            log.warn("UserId is null, using test user ID 1L for development.");
            userId = 1L;
            // throw new BusinessException(ErrorCode.UNAUTHORIZED); // 실제 운영 시
        }
        postService.deleteWriting(userId, postId);

        ApiResponse<Void> responseBody = ApiResponse.success(
                null,
                "W0003",
                "글 삭제에 성공했습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
    private final ReactionService reactionService;


    @PutMapping("/{postId}/reaction")
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
