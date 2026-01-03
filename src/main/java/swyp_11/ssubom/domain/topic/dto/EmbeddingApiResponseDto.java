package swyp_11.ssubom.domain.topic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class EmbeddingApiResponseDto {
    private Status status;
    private Result result;

    @Getter
    @NoArgsConstructor
    public static class Status {
        String code;
        String message;
    }

    @Getter
    @NoArgsConstructor
    public static class Result {
        private List<Double> embedding;
        private Integer inputTokens;
    }

    public boolean isSuccess() {
        return status !=null && "20000".equals(status.getCode());
    }

    public List<Double> getVector(){
        return (result != null) ? result.getEmbedding() : null;
    }
}
