package swyp_11.ssubom.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swyp_11.ssubom.domain.notification.service.NotificationService;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/stream")
    public SseEmitter connect(@AuthenticationPrincipal CustomOAuth2User user) {
        return notificationService.connect(user.getUserId());
    }
}