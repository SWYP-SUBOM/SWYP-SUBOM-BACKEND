package swyp_11.ssubom.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ClovaApiRequestDto {
    private List<Message> messages;
    private double temperature; // (0.5 기본값)
    private double topP;        // (0.8 기본값)
    private int maxCompletionTokens; // (512 기본값)
    private ResponseFormat responseFormat;

    @Getter
    @Builder
    public static class Message {
        private String role;
        private String content;
    }

    @Getter
    @Builder
    public static class ResponseFormat {
        private String type; // "json"

        // clova-feedback-schema.json 파일을 읽어 Map으로 변환한 것을 여기 넣기
        private Map<String, Object> schema;
    }
}
