package swyp_11.ssubom.domain.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp_11.ssubom.domain.admin.entity.Admin;
import swyp_11.ssubom.domain.admin.repository.AdminRepository;
import swyp_11.ssubom.domain.admin.service.QrCodeService;
import swyp_11.ssubom.domain.admin.service.TotpService;
import swyp_11.ssubom.domain.topic.dto.*;
import swyp_11.ssubom.domain.topic.entity.Status;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.entity.TopicGeneration;
import swyp_11.ssubom.domain.topic.service.TopicGenerationService;
import swyp_11.ssubom.domain.topic.service.TopicService;
import swyp_11.ssubom.domain.admin.dto.AdminLoginRequest;
import swyp_11.ssubom.domain.admin.dto.AdminLoginResponse;
import swyp_11.ssubom.domain.admin.service.AdminLoginService;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.global.security.util.SecurityUtil;

import java.time.LocalDate;

@Tag(name = "Admin 전용 ", description = " admin API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final TopicService topicService;
    private final TopicGenerationService topicGenerationService;
    private final AdminLoginService adminLoginService;
    private final TotpService totpService;
    private final AdminRepository adminRepository;
    private final SecurityUtil securityUtil;
    private final QrCodeService qrCodeService;
    @PostMapping("/manage/login")
    public ResponseEntity<AdminLoginResponse> login(
            @RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(adminLoginService.login(request));
    }

    @PostMapping("/2fa/setup")
    public ResponseEntity<byte[]> setup2fa() throws Exception {

        Admin admin = securityUtil.getCurrentAdmin();

        String secret = totpService.generateSecret();
        admin.enable2fa(secret);
        adminRepository.save(admin);

        String otpUrl = String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                "SSUBOM-ADMIN",
                admin.getEmail(),
                secret,
                "SSUBOM-ADMIN");

        byte[] qrImage = qrCodeService.generateQr(otpUrl);

        return ResponseEntity.ok()
                .body(qrImage);
    }

    @Operation(
            summary = "AI 토픽 자동 생성 버튼 API",
            description = """
                  비동기식 주제생성 호출 
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/topic/generation")
    public ApiResponse<TopicGenerationResponseDto> topicGeneration(){
        TopicGeneration tg = topicGenerationService.startGeneration();
        return ApiResponse.success(
                TopicGenerationResponseDto.from(tg),
                "AD0001",
                "토픽 생성 작업이 시작되었습니다"
        );
    }

    @Operation(
            summary = "AI 토픽 생성 상태 조회 API",
            description = """
                  비동기식 주제생성 상태 조회 
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/topic/generation/{generationId}")
    public ApiResponse<TopicGenerationResponseDto> getGenerationStatus(@PathVariable Long generationId){
        TopicGeneration tg = topicGenerationService.getGeneration(generationId);
        return ApiResponse.success(TopicGenerationResponseDto.from(tg),"AD0002",  "토픽 생성 상태 조회 성공");
    }

    @Operation(
            summary = "AI 토픽 직접 생성하는 API",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PostMapping("/topic/generation/{categoryId}")
    public ApiResponse<TodayTopicResponseDto> createTopic(@PathVariable Long categoryId, @RequestBody TopicCreationRequest request) {
        Topic savedTopic = topicService.generateTopicForCategory(
                categoryId,
                request.getTopicName(),
                request.getTopicType()
        );
        TodayTopicResponseDto dto = TodayTopicResponseDto.fromTopic(savedTopic);
        return ApiResponse.success(dto,"AD0003","관리자 질문 생성 성공");
    }

    @Operation(
            summary = "토픽 수정 API",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PatchMapping("/topic/{topicId}")
    public ApiResponse<TodayTopicResponseDto> updateTopic(@PathVariable Long topicId, @RequestBody TopicUpdateRequest request) {
        Topic savedTopic = topicService.updateTopic(topicId, request);
        TodayTopicResponseDto dto = TodayTopicResponseDto.fromTopic(savedTopic);
        return ApiResponse.success(dto,"AD0004","관리자 질문 수정 성공");
    }

    @Operation(
            summary = "토픽 삭제 API",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @DeleteMapping("/topic/{topicId}")
    public ApiResponse<Void> deleteTopic(@PathVariable Long topicId) {
        topicService.deleteTopic(topicId);
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "관리자 토픽 조회 API",
            description = """
                  필터 1 ) mode : ALL/APPROVED/PENDING/QUESTION/LOGICAL
                  필터 2 ) category : 1 ~ 5
                  필터 안 주면 기본값 mode : ALL , categoryId : 1,2,3,4,5 전체
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/topics")
    public ApiResponse<AdminTopicListResponse> getAdminTopics(@RequestParam(required = false ,defaultValue = "ALL") String mode , @RequestParam(required = false)Long categoryId) {
       return ApiResponse.success(topicService.getAdminTopics(mode,categoryId),"AD0005","관리자 질문 조회 성공");

    }

    @Operation(
            summary = "토픽 승인/미승인 체크 API",
            description = """
                  APPROVED/PENDING 상태변경
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PatchMapping("/topic/{topicId}/status")
    public ApiResponse<Void> updateTopicStatus(@PathVariable Long topicId, @RequestParam Status status){
        topicService.updateTopicStatus(topicId,status);
        return ApiResponse.success(null,"AD0006","질문 상태 변경 성공");
    }

    @Operation(
            summary = " 날짜예약 API",
            description = """
                 
            """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PatchMapping("/topic/{topicId}/reservation")
    public ApiResponse<Void> updateReservation(
            @PathVariable Long topicId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate usedAt
    ) {
        topicService.updateReservation(topicId, usedAt);
        return ApiResponse.success(null, "AD0007", "예약 변경 성공");
    }
}
