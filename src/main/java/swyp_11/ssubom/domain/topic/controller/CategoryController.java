package swyp_11.ssubom.domain.topic.controller;


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

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final TopicService topicService;
    @GetMapping("/api/categories/{categoryId}/question")
    public ApiResponse<TodayTopicResponseDto> getCategoryQuestion(@PathVariable("categoryId") Long categoryId) {
        TodayTopicResponseDto dto =topicService.ensureTodayPickedDto(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        return ApiResponse.success(dto,"S200","질문 조회 성공");
    }

    @GetMapping("/api/categories/{categoryId}/questions")
    public ApiResponse<TopicListResponse> getTopicCollection(@PathVariable("categoryId") Long categoryId, @RequestParam(name = "sort",defaultValue = "latest") String sort) {
        TopicListResponse dto = topicService.getAll(categoryId, sort);
            return ApiResponse.success(dto,"T0003","주제 조회 성공");
    }

}
