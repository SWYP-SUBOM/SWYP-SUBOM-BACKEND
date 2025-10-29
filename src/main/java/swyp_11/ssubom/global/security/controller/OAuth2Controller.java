package swyp_11.ssubom.global.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import swyp_11.ssubom.global.security.service.OAuth2JwtHeaderService;

@Controller
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2JwtHeaderService jwtHeaderService;

    @PostMapping("/oauth2-jwt-header")
    public String oauthJwtHeader(HttpServletRequest request, HttpServletResponse response) {
        return jwtHeaderService.oauth2JwtHeaderSet(request, response);
    }
}
