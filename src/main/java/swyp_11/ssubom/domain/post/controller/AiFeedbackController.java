package swyp_11.ssubom.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp_11.ssubom.domain.post.dto.AiFeedbackResultResponseDto;
import swyp_11.ssubom.domain.post.dto.AiFeedbackStartResponseDto;
import swyp_11.ssubom.domain.post.dto.PostUpdateResponse;
import swyp_11.ssubom.domain.post.service.AiFeedbackService;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.global.response.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class AiFeedbackController {
    private final AiFeedbackService aiFeedbackService;

    @PostMapping("/{postId}/ai-feedback")
    public ResponseEntity<ApiResponse<AiFeedbackStartResponseDto>> startAiFeedback(@PathVariable Long postId) {

        log.info("AI 피드백 생성 요청 (Post ID: {})", postId);

        // 서비스 호출 (서비스에서 'PROCESSING' 레코드를 생성하고 비동기 호출)
        AiFeedbackStartResponseDto responseDto = aiFeedbackService.startFeedbackGeneration(postId);

        ApiResponse<AiFeedbackStartResponseDto> responseBody = ApiResponse.success(
                responseDto,
                "AI001",
                "AI feedback 생성 완료(status=PROCESSING)"
        );

        // 비동기 작업이므로 202 Accepted 반환
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(responseBody);
    }

    @Operation(
            summary = "AI 피드백 조회 ",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/{postId}/ai-feedback/{aiFeedbackId}")
    public ResponseEntity<ApiResponse<AiFeedbackResultResponseDto>> getAiFeedback(
            @PathVariable Long postId,
            @PathVariable Long aiFeedbackId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        AiFeedbackResultResponseDto responseDto = aiFeedbackService.getAiFeedback(user.getUserId(),postId, aiFeedbackId);

        switch (responseDto.getStatus()) {
            case PROCESSING:
                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(ApiResponse.success(responseDto, "AI002", "AI 피드백이 아직 처리 중입니다."));
            case COMPLETED:
                return ResponseEntity.ok(ApiResponse.success(responseDto, "AI001", "AI 피드백 조회에 성공했습니다"));
            default:
                return ResponseEntity
                        .status(HttpStatus.NOT_ACCEPTABLE)
                        .body(ApiResponse.success(responseDto, "AI003", "AI 피드백을 제공하기엔, 제공된 컨텐츠 길이가 너무 짧습니다(_자)"));
        }
    }
}