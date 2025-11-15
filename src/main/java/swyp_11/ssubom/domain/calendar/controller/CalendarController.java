package swyp_11.ssubom.domain.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.domain.calendar.dto.CalendarResponse;
import swyp_11.ssubom.domain.calendar.service.CalendarService;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.global.response.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @Operation(
            summary = "캘린더 조회 API",
            description = """
                캘린더 상세 조회.
                비로그인 사용자는 캘린더 조회 불가.
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<CalendarResponse>> getCalendar(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam int year,
            @RequestParam int month
    ) {
        User loginUser = user.toEntity();
        CalendarResponse calendarResponse = calendarService.getCalendar(loginUser, year, month);
        return ResponseEntity.ok(ApiResponse.success(
                calendarResponse,
                "C0001",
                "내 캘린더 조회에 성공했습니다."));
    }
}