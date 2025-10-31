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
import swyp_11.ssubom.writing.dto.WritingCreateRequest;
import swyp_11.ssubom.writing.dto.WritingCreateResponse;
import swyp_11.ssubom.writing.service.WritingService;

@Slf4j
@RestController
@RequestMapping("/api/writings")
@RequiredArgsConstructor
public class WritingController {

    private final WritingService writingService;

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
}
