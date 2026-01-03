package swyp_11.ssubom.domain.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import swyp_11.ssubom.domain.post.dto.ClovaApiRequestDto;
import swyp_11.ssubom.domain.post.dto.ClovaApiResponseDto;
import swyp_11.ssubom.domain.post.dto.HyperClovaResponseDto;
import org.springframework.core.io.Resource;
import swyp_11.ssubom.domain.topic.entity.TopicType;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.utils.SentenceSplitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HyperClovaService {

    private final WebClient webClient;

    private final ObjectMapper objectMapper;
    // 1. 프롬프트 파일 로드
    @Value("classpath:/static/logical-clova-feedback-system.txt")
    private Resource systemPromptResourceForLogicalTopic;

    @Value("classpath:/static/question-clova-feedback-system.txt")
    private Resource systemPromptResourceForQuestionTopic;

    @Value("classpath:/static/clova-feedback-schema.json")
    private Resource schemaResource;

    private String systemPromptForLogicalTopic;

    private String systemPromptForQuestionTopic;

    private Map<String, Object> responseSchema;

    public HyperClovaService(@Qualifier("clovaWebClient") WebClient webClient,
                             ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {
        // System 프롬프트 로드
        this.systemPromptForLogicalTopic = new String(
                systemPromptResourceForLogicalTopic.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        this.systemPromptForQuestionTopic = new String(
                systemPromptResourceForQuestionTopic.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        log.info("Clova system prompt loaded.");

        // JSON Schema 로드 및 Map으로 파싱
        String schemaJson = new String(
                schemaResource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        this.responseSchema = objectMapper.readValue(
                schemaJson,
                new TypeReference<Map<String, Object>>() {}
        );
        log.info("Clova response schema loaded.");
    }

    public HyperClovaResponseDto getFeedback(String writing, TopicType topicType, String topicCategoryName, String topicName) {
        // 0. BreakIterator로 문장 분리
        List<String> sentences = SentenceSplitter.split(writing);
        String content = constructIndexedContent(sentences, topicCategoryName, topicName);

        // 1. Naver API 요청 DTO 생성
        String targetSystemPrompt = (topicType == TopicType.QUESTION)
                ? this.systemPromptForQuestionTopic
                : this.systemPromptForLogicalTopic;

        // 요청 DTO 생성
        ClovaApiRequestDto apiRequest = ClovaApiRequestDto.builder()
                .messages(List.of(
                        ClovaApiRequestDto.Message.builder().role("system").content(targetSystemPrompt).build(),
                        ClovaApiRequestDto.Message.builder().role("user").content(content).build()
                ))
                .responseFormat(ClovaApiRequestDto.ResponseFormat.builder()
                        .type("json")
                        .schema(this.responseSchema)
                        .build())
                .thinking(ClovaApiRequestDto.Thinking.builder().effort("none").build())
                .temperature(0.5)
                .maxCompletionTokens(1024)
                .topP(0.8)
                .build();

        // 2. (로깅) API에 보낼 최종 요청 serialize
        String requestBodyJson = serializeRequest(apiRequest);
        log.info("[Clova Request] Body: {}", requestBodyJson);

        // 3. API 호출 (HCX-007 모델 사용 (이거만 쓸 수 있었음))
        String rawApiResponse = webClient.post()
                .uri("/v3/chat-completions/HCX-007")
                .bodyValue(requestBodyJson)
                .retrieve()
                .onStatus(status -> status.isError(), res ->
                        res.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error("[Clova Error Response] Status: {}, Body: {}", res.statusCode().value(), errorBody);
                                return Mono.error(new RuntimeException("Clova Request Failed: " + errorBody));
                            })
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .block();

        // 4. (로깅) API에서 받은 원본(Raw) 응답 기록 (디버깅 핵심)
        log.info("[Clova Response] Raw: {}", rawApiResponse);

        // 5. [1단계 파싱] Naver API 래퍼(Wrapper) 파싱
        ClovaApiResponseDto apiResponse = parseApiResponse(rawApiResponse, ClovaApiResponseDto.class);

        if (!apiResponse.isSuccess()) {
            log.error("Clova API 호출 실패: code={}, message={}",
                    apiResponse.getStatus().getCode(), apiResponse.getStatus().getMessage());
            throw new BusinessException(ErrorCode.AIFEEDBACK_API_FAILED);
        }

        // 6. [2단계 파싱] LLM이 생성한 JSON 문자열(content)을 우리 DTO로 파싱
        String llmResultJson = apiResponse.getLlmResultContent();
        if (llmResultJson == null) {
            log.error("LLM 결과 파싱 실패: 'result.message.content'가 null입니다.");
            throw new BusinessException(ErrorCode.AIFEEDBACK_PARSE_FAILED);
        }

        HyperClovaResponseDto feedbackDto = parseApiResponse(
                llmResultJson.trim(),
                HyperClovaResponseDto.class
        );

        log.info("Clova API 2-stage parsing successful.");

        mapIndexToOriginalText(feedbackDto, sentences);

        return feedbackDto;
    }

    private String constructIndexedContent(List<String> sentences, String category, String topic) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("주제 카테고리: %s\n주제: %s\n\n내용:\n", category, topic));
        for (int i = 0; i < sentences.size(); i++) {
            sb.append("[").append(i).append("] ").append(sentences.get(i)).append("\n");
        }
        return sb.toString();
    }

    private void mapIndexToOriginalText(HyperClovaResponseDto dto, List<String> sentences) {
        if (dto.getImprovementPoints() == null) return;

        for (HyperClovaResponseDto.FeedbackPoint point : dto.getImprovementPoints()) {
            int idx = point.getSentenceIndex();

            // 인덱스가 유효하면 원본 문장을 넣어줌
            if (idx >= 0 && idx < sentences.size()) {
                point.setOriginalText(sentences.get(idx));
            } else {
                // 인덱스가 -1이거나 범위를 벗어나면 원본 텍스트 비움 (전체 피드백 등)
                point.setOriginalText(null);
            }
        }
    }


    //------ helper------

    private String serializeRequest(ClovaApiRequestDto requestDto) {
        try {
            return objectMapper.writeValueAsString(requestDto);
        } catch (JsonProcessingException e) {
            log.error("Clova 요청 JSON 직렬화 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // JSON 문자열을 DTO 객체로 파싱
    private <T> T parseApiResponse(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패 (Target: {}): {}", clazz.getSimpleName(), jsonString, e);
            throw new BusinessException(ErrorCode.AIFEEDBACK_PARSE_FAILED);
        }
    }


}
