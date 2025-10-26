package swyp_11.ssubom.global.error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 모든 error code를 위한 하나의 SoT
// define errors per domain
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //common errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid input value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "An unexpected server error occurred"),

    // User errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),

    // Topic errors

    // Writing errors

    // Feed errors

    // Reaction errors

    // Notification errors

    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
