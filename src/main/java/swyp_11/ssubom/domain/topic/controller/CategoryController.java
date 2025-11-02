package swyp_11.ssubom.domain.topic.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import swyp_11.ssubom.domain.topic.dto.TopicCollectionResponse;
import swyp_11.ssubom.domain.topic.dto.TopicListResponse;
import swyp_11.ssubom.domain.topic.service.TopicService;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.domain.topic.dto.TodayTopicResponseDto;

import java.util.List;


@Tag(name = "Topic", description = "category , topic 관련 API")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final TopicService topicService;

    @Operation(
            summary = "카테고리 오늘의 질문 조회",
            description = "오늘의 질문을 반환한다.",
            security = { @SecurityRequirement(name = "bearerAuth") },
            parameters = {
                    @Parameter(name = "categoryId", in = ParameterIn.PATH, required = true, description = "카테고리 ID", example = "1")
            }
    )

    @GetMapping("/api/categories/{categoryId}/question")
    public ApiResponse<TodayTopicResponseDto> getCategoryQuestion(@PathVariable("categoryId") Long categoryId) {
        TodayTopicResponseDto dto =topicService.ensureTodayPickedDto(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        return ApiResponse.success(dto,"S200","질문 조회 성공");
    }

    @Operation(
            summary = "주제 모아보기",
            description = "해당 카테고리에 대한 최대 30개의 주제를 조회.",
            security = { @SecurityRequirement(name = "bearerAuth") },
            parameters = {
                    @Parameter(name = "categoryId", in = ParameterIn.PATH, required = true, description = "카테고리 ID", example = "1"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, required = false, description = "정렬 기준 (latest | popular)", example = "latest")
            }
    )
    @GetMapping("/api/categories/{categoryId}/questions")
    public ApiResponse<TopicListResponse> getTopicCollection(@PathVariable("categoryId") Long categoryId, @RequestParam(name = "sort",defaultValue = "latest") String sort) {
        TopicListResponse dto = topicService.getAll(categoryId, sort);
            return ApiResponse.success(dto,"T0003","주제 조회 성공");
    }

}
