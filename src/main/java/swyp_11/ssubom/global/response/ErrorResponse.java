package swyp_11.ssubom.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.global.error.ErrorCode;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null이 아닌 필드만 JSON에 포함
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<FieldError> fieldErrors; // 유효성 검사 에러 목록

    // 단일 ErrorCode로 ErrorResponse 생성
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getErrorCode())
                .message(errorCode.getErrorMessage())
                .build();
    }

    // ErrorCode와 fieldErrors 리스트로 ErrorResponse 생성
    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
                .code(errorCode.getErrorCode())
                .message(errorCode.getErrorMessage())
                .fieldErrors(fieldErrors)
                .build();
    }

    @Getter
    public static class FieldError {
        private final String field;
        private final Object rejectedValue;
        private final String reason;

        public FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }
    }
}
