package swyp_11.ssubom.writing.controller;


import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.writing.dto.*;
import swyp_11.ssubom.writing.service.ReactionService;
import swyp_11.ssubom.writing.service.WritingService;

@Slf4j
@RestController
@RequestMapping("/api/writings")
@RequiredArgsConstructor
public class WritingController {

    private final WritingService writingService;
    private final ReactionService reactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<WritingCreateResponse>> createWriting(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WritingCreateRequest request
    ) {
        if (userId == null) {
            // 임시 예외 처리 또는 테스트용 ID 할당
            log.warn("UserId is null, using test user ID 1L for development.");
            userId = 1L;
            // throw new BusinessException(ErrorCode.UNAUTHORIZED); // 실제 운영 시
        }

        WritingCreateResponse writingCreateResponse = writingService.createWriting(userId, request);
        ApiResponse<WritingCreateResponse> responseBody = ApiResponse.success(
                writingCreateResponse,
                "W0001",
                "글 임시저장에 성공했습니다"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<WritingUpdateResponse>> updateWriting(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody WritingUpdateRequest request
    ) {
        if (userId == null) {
            // 임시 예외 처리 또는 테스트용 ID 할당
            log.warn("UserId is null, using test user ID 1L for development.");
            userId = 1L;
            // throw new BusinessException(ErrorCode.UNAUTHORIZED); // 실제 운영 시
        }

        WritingUpdateResponse writingUpdateResponse = writingService.updateWriting(
                userId,
                postId,
                request
        );

        ApiResponse<WritingUpdateResponse> responseBody = ApiResponse.success(
                writingUpdateResponse,
                "W0002",
                "임시저장한 글 수정에 성공했습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @DeleteMapping("/{postId}")
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
        writingService.deleteWriting(userId, postId);

        ApiResponse<Void> responseBody = ApiResponse.success(
                null,
                "W0003",
                "글 삭제에 성공했습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

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

    @DeleteMapping("/{postId}/reaction")
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
