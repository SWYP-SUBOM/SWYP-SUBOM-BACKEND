package swyp_11.ssubom.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.dto.UserProfileResponse;
import swyp_11.ssubom.domain.user.service.UserService;
import swyp_11.ssubom.global.response.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@AuthenticationPrincipal CustomOAuth2User user) {
        UserProfileResponse userProfileResponse = userService.getUserProfile(user.toEntity());
        return ResponseEntity.ok(ApiResponse.success(userProfileResponse, "U0001", "프로필 조회에 성공했습니다."));
    }
}