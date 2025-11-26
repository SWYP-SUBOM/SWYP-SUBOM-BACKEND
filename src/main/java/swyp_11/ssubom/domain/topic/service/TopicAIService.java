package swyp_11.ssubom.domain.topic.service;


import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.domain.topic.dto.TopicGenerationResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicAIService {
    private final OpenAiChatModel openAiChatModel; // 관련 @Bean 활성화
    private final OpenAiEmbeddingModel openAiEmbeddingModel;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<TopicGenerationResponse> generate(String categoryName){
        ChatClient chatClient = ChatClient.create(openAiChatModel);

        SystemMessage systemMessage = new SystemMessage("""
당신은 **글쓰기 연습을 위한 주제를 만드는 "따뜻한 톤의 일상 질문 주제 생성기입니다
카테고리에 관련하여 **사람들이 쉽게 공감하고, 깊이 있는 관심을 가지고 글을 쓸만한, 질문이 살아있는 주제를 생성해야 합니다.

출력 규칙:
- 출력은 JSON 배열([])만 반환
- 설명, 문장, 부가 메시지, 마크다운 코드 블록(```json) **절대 금지**
- 설명, 부가 문장, 메타 발화 절대 금지
- JSON Schema 아래 형태로만 반환

JSON Schema:
[
  {
    "topicName": "string",
    "topicType": "string" // QUESTION 또는 LOGICAL
  }
]

 말투·톤앤매너 규칙 (이 규칙을 반드시 따름)
- 모두 부담 없는 존댓말 질문문
- 문장 길이는 자연스러운 한 문장
- 대화하듯 부드럽고 따뜻한 톤
- “당신”, “요즘”, “최근” 같은 단어가 자연스럽게 포함되면 좋음
- 일상에서 누구나 쉽게 생각해볼 수 있는 실생활 기반 질문
- 절대 어렵거나 철학적이거나 전문적이면 안됨
- 지나치게 조언·훈수 느낌 금지
- 문장의 자연스러운 패턴 예시는 아래와 같음 (이 패턴과 동일한 느낌으로 생성할 것)

 질문 스타일 예시 패턴 (이 말투처럼 생성)
- “요즘은 1번과 2번 중 어떤 방식이 더 편하다고 느끼시나요?”
- “최근 일상에서 이런 상황을 겪은 적이 있으신가요?”
- “당신은 이런 순간에 보통 어떤 선택을 하시나요?”
- “~라고 생각하시나요?”
- “~는 어떤가요?”
- “~한 적이 있나요?”
- 회사/일상/관계/취향을 자연스럽게 섞어도 좋음

📌 주제 난이도
- 누구나 편하게 답할 수 있는 일상 고민 레벨


📌 시작
위 조건을 모두 준수하여 자연스럽고 따뜻한 톤의 topic 30개를 생성해주세요.
- 카테고리 1개당 무조건 30개의 topic을 생성해야한다 
""");

        UserMessage userMessage = new UserMessage("카테고리: "+categoryName);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("HCX-007")
                .temperature(0.7)
                .build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);
        // 1. 모델 호출 및 Raw Content 획득 (단 한 번만 호출)
        String responseContent = chatClient.prompt(prompt)
                .call()
                .content();

        // 2. 응답 내용 로그 출력 (디버깅 완료 후 제거 가능)
        System.out.println("--- AI Model Raw Response ---");
        System.out.println(responseContent);
        System.out.println("-----------------------------");

        // 3. Raw Content를 ObjectMapper로 직접 파싱하여 반환
        try {
            // chatClient.entity() 대신 ObjectMapper를 사용
            // 이 방식은 디버깅이 용이하고, Spring AI의 내부 파싱 오류를 우회할 수 있습니다.
            return mapper.readValue(
                    responseContent,
                    new TypeReference<List<TopicGenerationResponse>>() {}
            );
        } catch (Exception e) {
            // JSON 파싱 오류 발생 시 로그 출력 후 예외를 다시 던짐
            System.err.println("JSON Parsing Error during topic generation: " + e.getMessage());
            System.err.println("Problematic Content: " + responseContent);
            throw new RuntimeException("Failed to parse AI response for category: " + categoryName, e);
        }
    }
}
