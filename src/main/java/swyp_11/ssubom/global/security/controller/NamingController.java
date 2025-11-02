package swyp_11.ssubom.global.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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


@Tag(name = "UserName", description = "사용자이름 관련 API")
@RequiredArgsConstructor
@RestController
public class NamingController {

   private final NameService nameService;
    @Operation(
            summary = "이름 입력전달",
            description = "저장 후 본문 없이 성공만 반환",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )

    @PostMapping("/api/naming")
    public ApiResponse<Void> naming(@RequestParam String name , @AuthenticationPrincipal OAuth2User user ) {
        String kakaoId = user.getAttribute("kakaoId");
         nameService.saveName(kakaoId,name);
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "이름 입력받기",
            description = "사용자의 이름을 입력받습니다",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = """
                        {
                          "success": true,
                          "code": "S200",
                          "message": "정상 처리되었습니다",
                          "data": "몬스터"
                        }
                    """
                            )
                    )
            )
    })
    @GetMapping("/api/naming")
    public ApiResponse<String> naming(@AuthenticationPrincipal OAuth2User user) {
        String kakaoId = user.getAttribute("kakaoId");
        return ApiResponse.success(nameService.getName(kakaoId));
    }
}
