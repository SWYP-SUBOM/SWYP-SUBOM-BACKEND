package swyp_11.ssubom.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp_11.ssubom.domain.notification.dto.FcmTokenRequest;
import swyp_11.ssubom.domain.notification.service.FcmService;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.global.response.ApiResponse;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
@Slf4j
public class FcmController {

    private final FcmService fcmService;

    @Operation(
            summary = "FCM token 생성",
            description = """
                FCM token 생성
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/token")
    public ResponseEntity<ApiResponse<Boolean>> registerToken(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody FcmTokenRequest request) {

        fcmService.saveToken(user.getUserId(), request.getToken());
        return ResponseEntity.ok(ApiResponse.success(true,"P0000","FCM Push 알림 토큰 생성에 성공했습니다."));
    }

    @Operation(
            summary = "FCM token 삭제",
            description = """
                FCM token 삭제
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @DeleteMapping("/token")
    public ResponseEntity<ApiResponse<Boolean>> deleteToken(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam String token) {

        fcmService.deleteToken(user.getUserId(), token);
        return ResponseEntity.ok(ApiResponse.success(true,"P0001","FCM Push 알림 토큰 삭제에 성공했습니다."));
    }
}