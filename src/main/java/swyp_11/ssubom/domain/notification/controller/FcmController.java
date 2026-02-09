package swyp_11.ssubom.domain.notification.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp_11.ssubom.domain.notification.service.FcmService;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
@Slf4j
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody FcmTokenRequest request) {

        fcmService.saveToken(user.getUserId(), request.getToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteToken(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam String token) {

        fcmService.deleteToken(user.getUserId(), token);
        return ResponseEntity.ok().build();
    }
}

@Getter
@Setter
class FcmTokenRequest {
    private String token;
}