package swyp_11.ssubom.global.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.global.security.service.NameService;

@RequiredArgsConstructor
@RestController
public class NamingController {

   private final NameService nameService;

    @PostMapping("/naming")
    public ApiResponse<Void> naming(@RequestParam String name , @AuthenticationPrincipal OAuth2User user ) {
        String kakaoId = user.getAttribute("kakaoId");
         nameService.saveName(kakaoId,name);
        return ApiResponse.success(null);
    }

    @GetMapping("/naming")
    public ApiResponse<String> naming(@AuthenticationPrincipal OAuth2User user) {
        String kakaoId = user.getAttribute("kakaoId");
        return ApiResponse.success(nameService.getName(kakaoId));
    }
}
