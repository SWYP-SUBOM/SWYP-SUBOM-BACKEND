package swyp_11.ssubom.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClovaApiResponseDto {
    private Status status;
    private Result result;

    @Getter
    @NoArgsConstructor
    public static class Status {
        private String code;
        private String message;
    }

    @Getter
    @NoArgsConstructor
    public static class Result {
        private Message message;
        private Usage usage;
    }

    @Getter
    @NoArgsConstructor
    public static class Message { // (핵심) LLM이 생성한 JSON '문자열'이 여기 담김
        private String role;
        private String content;
        private String finishReason;
    }

    @Getter
    @NoArgsConstructor
    public static class Usage {
        private Integer completionTokens;
        private Integer promptTokens;
        private Integer totalTokens;
    }

    // 헬퍼 메서드: 1단계 파싱 후 LLM이 생성한 JSON 문자열 반환
    public String getLlmResultContent() {
        if (result != null && result.message != null) {
            return result.message.content;
        }
        return null;
    }

    // 헬퍼 메서드: API 호출 자체가 성공했는지 확인
    public boolean isSuccess() {
        return status != null && "20000".equals(status.code);
    }
}
