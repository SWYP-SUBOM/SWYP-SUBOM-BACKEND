package swyp_11.ssubom.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.service.NameService;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.response.ApiResponse;


@Tag(name = "UserName", description = "사용자이름 관련 API")
@RequiredArgsConstructor
@RestController
public class NamingController {

   private final NameService nameService;

    @Operation(
            summary = "사용자 이름 저장 API",
            description = """
                        로그인 성공 시 URL 쿼리 스트링에 name=no 라면 회원가입 해야 하는 사용자
                        이름 저장 API 요청 필요
                    """,
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
                                    """)))})
    @PostMapping("/api/naming")
    public ApiResponse<Void> naming(@RequestParam(value = "name") String name , @AuthenticationPrincipal CustomOAuth2User user ) {
        Long userId = user.getUserId();
        if(userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        nameService.saveName(userId,name);
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "이름 조회 API",
            description = """
                        이름 입력 후 온보딩 페이지에서 보여주는 사용자 이름
                    """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/api/naming")
    public ApiResponse<String> naming(@AuthenticationPrincipal CustomOAuth2User user) {
        Long userId = user.getUserId();
        if(userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return ApiResponse.success(nameService.getName(userId));
    }
}
