package swyp_11.ssubom.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swyp_11.ssubom.domain.notification.dto.NotificationListResponse;
import swyp_11.ssubom.domain.notification.service.NotificationService;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.global.response.ApiResponse;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "알림 연결 API",
            description = """
                SSE 방식으로 알림 연결
                사용자 로그인 SUCCESS 시 요청
                읽지 않은 알림이 있으면 badge 표시
                사용자 로그아웃 시 SSE close
            """,
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/stream")
    public SseEmitter connect(@AuthenticationPrincipal CustomOAuth2User user) {
        return notificationService.connect(user.getUserId());
    }

    @Operation(
            summary = "알림 리스트 조회 API",
            description = """
                다른 사용자들이 내 글에 반응을 남기면 count 집계
                알림 리스트 조회 시 리스트에 있는 알림 읽음 처리
            """,
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping
    public ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeUpdatedAt
    ) {
        Long userId = user.getUserId();
        if (beforeUpdatedAt == null) {
            beforeUpdatedAt = LocalDateTime.now();
        }
        NotificationListResponse response = notificationService.getNotifications(userId, limit, beforeUpdatedAt);
        return ResponseEntity.ok(ApiResponse.success(response, "N0001", "알림 리스트 조회에 성공했습니다."));
    }
}