package swyp_11.ssubom.domain.notification.controller;

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

    @GetMapping("/stream")
    public SseEmitter connect(@AuthenticationPrincipal CustomOAuth2User user) {
        return notificationService.connect(user.getUserId());
    }

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