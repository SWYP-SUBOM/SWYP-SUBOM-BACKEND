package swyp_11.ssubom.domain.topic.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import swyp_11.ssubom.domain.topic.service.TopicService;
import swyp_11.ssubom.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.domain.topic.dto.TodayTopicResponseDto;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final TopicService topicService;
    @GetMapping("/api/categories/{categoryId}/question")
    public ApiResponse<TodayTopicResponseDto> getCategoryQuestion(@PathVariable("categoryId") Long categoryId) {
        TodayTopicResponseDto dto =topicService.ensureTodayPickedDto(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "잠시만 기다려주세요"));
        return ApiResponse.success(dto);
    }
}
