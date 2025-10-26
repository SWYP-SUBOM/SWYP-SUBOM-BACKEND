package swyp_11.ssubom.domain.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "User", description = "사용자 관리 API")
@Log4j2
public class testController {

    @GetMapping
    @Operation(summary = "서버 상태 확인", description = "서버가 정상적으로 동작하는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public Map<String, Object> healthCheck() {
        log.info("Health check called");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "동작 중");
        response.put("timestamp", System.currentTimeMillis());
        response.put("currentTime", LocalDateTime.now().toString());
        response.put("message", "Server is running");

        return response;
    }

    @GetMapping("/swagger")
    @Operation(summary = "Swagger 연동 테스트")
    public Map<String, String> swaggerTest() {

        Map<String, String> response = new HashMap<>();
        response.put("swagger", "enabled");
        response.put("version", "2.0");

        return response;
    }
}
