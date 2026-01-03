package swyp_11.ssubom.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.domain.user.service.ReissueService;


@Tag(name = "refresh token", description = "토큰 재발급")
@RestController
@ResponseBody
@RequiredArgsConstructor
public class ReissueController {
    private final ReissueService reissueService;

    @Operation(
            summary = "토큰 재발급 API",
            description = """
                token 만료 시 재발급
            """
    )
    @PostMapping("/api/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return reissueService.reissue(request, response);
    }
}
