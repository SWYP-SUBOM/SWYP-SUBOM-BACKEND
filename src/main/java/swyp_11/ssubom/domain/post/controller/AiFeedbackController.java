package swyp_11.ssubom.domain.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.domain.post.dto.AiFeedbackStartResponseDto;
import swyp_11.ssubom.domain.post.dto.PostUpdateResponse;
import swyp_11.ssubom.domain.post.service.AiFeedbackService;
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
}
