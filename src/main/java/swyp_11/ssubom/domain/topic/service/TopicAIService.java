package swyp_11.ssubom.domain.topic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import swyp_11.ssubom.domain.post.dto.ClovaApiRequestDto;
import swyp_11.ssubom.domain.post.dto.ClovaApiResponseDto;
import swyp_11.ssubom.domain.topic.dto.EmbeddingApiResponseDto;
import swyp_11.ssubom.domain.topic.dto.TopicGenerationResponse;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Slf4j
@Service
@RequiredArgsConstructor
public class TopicAIService {

    @Qualifier("clovaWebClient")
    private final WebClient clovaWebClient;
    private final ObjectMapper objectMapper;

    @Value("classpath:/static/topic/topic-system.txt")
    private Resource topicSystem;

    @Value("classpath:/static/topic/topic-schema.json")
    private Resource topicSchema;

    private String systemPrompt;
    private Map<String, Object> schema;

    @PostConstruct
    public void init() throws IOException {
        this.systemPrompt = new String(topicSystem.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String schemaJson = new String(topicSchema.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        this.schema = objectMapper.readValue(schemaJson, new TypeReference<Map<String, Object>>() {});
    }

    public List<TopicGenerationResponse> generateTopics(String categoryName){
            String userPrompt= "카테고리 : "+categoryName;
        ClovaApiRequestDto requestDto = ClovaApiRequestDto.builder()
                .messages(List.of(
                        ClovaApiRequestDto.Message.builder()
                                .role("system")
                                .content(systemPrompt)
                                .build(),
                        ClovaApiRequestDto.Message.builder()
                                .role("user")
                                .content(userPrompt)
                                .build()
                ))
                .responseFormat(
                        ClovaApiRequestDto.ResponseFormat.builder()
                                .type("json")
                                .schema(schema)
                                .build()
                )
                .thinking(ClovaApiRequestDto.Thinking.builder()
                        .effort("none")
                        .build()
                )
                .temperature(0.7)
                .maxCompletionTokens(5000)
                .topP(0.8)
                .build();

        // 요청 JSON 로깅용
        String requestJson = writeJson(requestDto);
        log.info("[Topic Clova Request] {}", requestJson);

        // 2. 클로바 호출
        String rawResponse = clovaWebClient.post()
                .uri("/v3/chat-completions/HCX-007")
                .bodyValue(requestJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("[Topic Clova Response Raw] {}", rawResponse);
        ClovaApiResponseDto apiResponse = readJson(rawResponse, ClovaApiResponseDto.class);

        if (!apiResponse.isSuccess()) {
            log.error("Topic Clova API 실패 code={}, message={}",
                    apiResponse.getStatus().getCode(),
                    apiResponse.getStatus().getMessage());
            throw new RuntimeException("Clova topic API failed");
        }


        // 4. 2단계: result.message.content 에 있는 JSON 배열 → List<TopicGenerationResponse>
        String llmJson = apiResponse.getLlmResultContent();
        log.info("[Topic LLM JSON Only] {}", llmJson);

        if (llmJson == null) {
            throw new RuntimeException("Topic LLM content is null");
        }

        try {
            return objectMapper.readValue(
                    llmJson,
                    new TypeReference<List<TopicGenerationResponse>>() {}
            );
        } catch (IOException e) {
            log.error("Topic JSON parse error. content={}", llmJson, e);
            throw new RuntimeException("Topic LLM JSON parse failed", e);
        }
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    private <T> T readJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }

    public List<Double> getEmbedding(String text){
        // 429 TOO_MANY_REQUESTS 방지
        try {
            Thread.sleep(250);  // 0.25초 딜레이
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        Map<String,Object> request = Map.of(
                "text", text);

        String raw = clovaWebClient.post()
                .uri("/v1/api-tools/embedding/v2")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Embedding raw data: {}", raw);

        EmbeddingApiResponseDto response;
        // wrapper 파싱
        try {
             response = objectMapper.readValue(raw, EmbeddingApiResponseDto.class);
        }catch (Exception e){
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }

        if(!response.isSuccess()){
            throw new RuntimeException("Embedding API failed: " + response.getStatus().getMessage());
        }
        List<Double> vector = response.getVector();

        if(vector.isEmpty()||vector.size()==0){
            throw new RuntimeException("Embedding vector is empty");
        }
        return vector;
    }
}
