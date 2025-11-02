package swyp_11.ssubom.global.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "jwt header", description = "엑세스 토큰 헤더 요청")
public class OAuth2Controller {
    private final OAuth2JwtHeaderService oAuth2JwtHeaderService;

    @PostMapping("/api/oauth2-jwt-header")
    @Operation(
            summary = "액세스 토큰 헤더 요청",
            description = "액세스 토큰을 헤더로 이동"
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
        return  ApiResponse.success(oAuth2JwtHeaderService.oauth2JwtHeaderSet(request, response));
    }
}
