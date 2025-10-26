package swyp_11.ssubom.global.response;

import lombok.Getter;
import swyp_11.ssubom.global.error.ErrorCode;

@Getter
public class ErrorResponse {

    private final String errorCode;
    private final String errorMessage;

    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());
    }
}
