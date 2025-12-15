package swyp_11.ssubom.domain.topic.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import swyp_11.ssubom.domain.topic.dto.TodayTopicResponseDto;
import swyp_11.ssubom.domain.topic.dto.TopicCreationRequest;
import swyp_11.ssubom.domain.topic.dto.TopicUpdateRequest;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.service.TopicAIService;
import swyp_11.ssubom.domain.topic.service.TopicGenerationService;
import swyp_11.ssubom.domain.topic.service.TopicService;
import swyp_11.ssubom.global.response.ApiResponse;

@Tag(name = "Admin 페이지전용 ", description = " topic 관련 admin API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final TopicService topicService;
    private final TopicGenerationService topicGenerationService;

    @PostMapping("/topic/generation")
    public ApiResponse<Void> topicGeneration(){
        topicGenerationService.generateTopics();
        return ApiResponse.success(null,"AD0001","질문 자동 생성 성공");
    }

    @PostMapping("/topic/generation/{categoryId}")
    public ApiResponse<TodayTopicResponseDto> createTopic(@PathVariable Long categoryId, @RequestBody TopicCreationRequest request) {
        Topic savedTopic = topicService.generateTopicForCategory(
                categoryId,
                request.getTopicName(),
                request.getTopicType()
        );
        TodayTopicResponseDto dto = TodayTopicResponseDto.fromTopic(savedTopic);
        return ApiResponse.success(dto,"AD0002","질문 생성 성공");
    }

    @PutMapping("/topic/generation/{topicId}")
    public ApiResponse<TodayTopicResponseDto> updateTopic(@PathVariable Long topicId, @RequestBody TopicUpdateRequest request) {
        Topic savedTopic = topicService.updateTopic(topicId, request);
        TodayTopicResponseDto dto = TodayTopicResponseDto.fromTopic(savedTopic);
        return ApiResponse.success(dto,"AD0003","질문 수정 성공");
    }

    @DeleteMapping("/topic/generation/{topicId}")
    public ApiResponse<Void> deleteTopic(@PathVariable Long topicId) {
        topicService.deleteTopic(topicId);
        return ApiResponse.success(null);
    }

}
