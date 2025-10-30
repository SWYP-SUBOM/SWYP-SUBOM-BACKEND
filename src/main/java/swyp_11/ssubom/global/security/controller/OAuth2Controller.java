package swyp_11.ssubom.global.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.global.security.service.OAuth2JwtHeaderService;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2JwtHeaderService oAuth2JwtHeaderService;

    @PostMapping("/api/oauth2-jwt-header")
    public ApiResponse<String> oauthJwtHeader(HttpServletRequest request, HttpServletResponse response) {
        return  ApiResponse.success(oAuth2JwtHeaderService.oauth2JwtHeaderSet(request, response));
    }
}
