package swyp_11.ssubom.domain.topic.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import swyp_11.ssubom.domain.topic.dto.*;
import swyp_11.ssubom.domain.topic.entity.Status;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.service.TopicAIService;
import swyp_11.ssubom.domain.topic.service.TopicGenerationService;
import swyp_11.ssubom.domain.topic.service.TopicService;
import swyp_11.ssubom.global.response.ApiResponse;

import java.util.List;

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
        return ApiResponse.success(null,"AD0001","관리자 질문 자동 생성 성공");
    }

    @PostMapping("/topic/generation/{categoryId}")
    public ApiResponse<TodayTopicResponseDto> createTopic(@PathVariable Long categoryId, @RequestBody TopicCreationRequest request) {
        Topic savedTopic = topicService.generateTopicForCategory(
                categoryId,
                request.getTopicName(),
                request.getTopicType()
        );
        TodayTopicResponseDto dto = TodayTopicResponseDto.fromTopic(savedTopic);
        return ApiResponse.success(dto,"AD0002","관리자 질문 생성 성공");
    }

    @PatchMapping("/topic/{topicId}")
    public ApiResponse<TodayTopicResponseDto> updateTopic(@PathVariable Long topicId, @RequestBody TopicUpdateRequest request) {
        Topic savedTopic = topicService.updateTopic(topicId, request);
        TodayTopicResponseDto dto = TodayTopicResponseDto.fromTopic(savedTopic);
        return ApiResponse.success(dto,"AD0003","관리자 질문 수정 성공");
    }

    @DeleteMapping("/topic/{topicId}")
    public ApiResponse<Void> deleteTopic(@PathVariable Long topicId) {
        topicService.deleteTopic(topicId);
        return ApiResponse.success(null);
    }


    @GetMapping("/topics")
    public ApiResponse<AdminTopicListResponse> getAdminTopics(@RequestParam(required = false ,defaultValue = "ALL") String mode , @RequestParam(required = false)Long categoryId) {
       return ApiResponse.success(topicService.getAdminTopics(mode,categoryId),"AD0004","관리자 질문 조회 성공");

    }

    @PatchMapping("/topic/{topicId}/status")
    public ApiResponse<Void> updateTopicStatus(@PathVariable Long topicId, @RequestParam Status status){
        topicService.updateTopicStatus(topicId,status);
        return ApiResponse.success(null,"AD0005","질문 상태 변경 성공");
    }
}
