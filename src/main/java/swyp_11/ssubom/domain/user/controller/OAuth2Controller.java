package swyp_11.ssubom.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.service.OAuth2JwtHeaderService;
import swyp_11.ssubom.domain.user.service.UserService;
import swyp_11.ssubom.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "oauth2 액세스토큰 , 회원탈퇴", description = "oauth2 관련 엑세스토큰 요청 , 회원탈퇴")
public class OAuth2Controller {

    private final OAuth2JwtHeaderService oAuth2JwtHeaderService;
    private final UserService userService;

    @PostMapping("/api/oauth2-jwt-header")
    @Operation(
            summary = "액세스 토큰 헤더 요청 API",
            description = """
                액세스 토큰을 헤더로 이동
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            headers = {
                    @Header(
                            name = "access",
                            description = "쿠키에서 꺼낸 액세스 토큰(JWT)",
                            schema = @Schema(type = "string"),
                            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    )
            }
    )
    public ApiResponse<String> oauthJwtHeader(HttpServletRequest request, HttpServletResponse response) {
        return ApiResponse.success(oAuth2JwtHeaderService.oauth2JwtHeaderSet(request, response));
    }

    @PostMapping("/api/unregister")
    @Operation(
            summary = "회원 탈퇴 API",
            description = """
                users db is_deleted true 로 변경 및 kakaoId값 null로 변경
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ApiResponse<String> unregister(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Authentication authentication,
                                          @AuthenticationPrincipal CustomOAuth2User user) {
        String kakaoId = user.getKakaoId();
        userService.userDelete(kakaoId);

        Cookie refreshCookie = new Cookie("refresh", null);
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);

        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ApiResponse.success("U0001",
                "회원탈퇴 성공");
    }
}
