package swyp_11.ssubom.global.error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 모든 error code를 위한 하나의 SoT
// define errors per domain
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // == Common Errors ==

    //1. @valid 유효성 검사 실패
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid input value"),
    // 2. HTTP 메소드가 잘못되었을 때
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "HTTP method not allowed"),
    // 3. Request body의 JSON 형식이 잘못되었을 때
    INVALID_JSON(HttpStatus.BAD_REQUEST, "C003", "Malformed JSON in request body"),
    // 4. NOT Authenticated
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "C005", "Authentication is required"),
    // 5. NOT Authorized (e.g., 남의 글 수정)
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "You do not have permission to perform this action"),
    // 6. 요청한 리소스가 존재하지 않을 때
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C008", "Resource not found"),
    // 7. 위 모든 것에 해당하지 않는, 예상치 못한 서버 내부 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C010", "An unexpected server error occurred"),

    // == Domain-Specific Errors ==
    // User errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),

    // Topic errors
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "카테고리를 찾을 수 없습니다."),
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "T002", "질문을 찾을 수 없습니다."),


    // Writing errors (API 명세 기반)
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "글을 찾을 수 없습니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "P002", "유효하지 않은 글 상태 변경입니다."),
    FORBIDDEN_WRITING_MODIFICATION(HttpStatus.FORBIDDEN, "P003", "이 글을 수정/삭제할 권한이 없습니다."),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "P004", "내용이 너무 깁니다."),
    NICKNAME_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "P005", "닉네임 생성에 실패했습니다. 잠시 후 다시 시도해주세요."),
    CANNOT_DELETE_PUBLISHED_POST(HttpStatus.BAD_REQUEST, "P006", "게시된 글은 삭제할 수 없습니다."),

    // Feed errors
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "피드를 찾을 수 없습니다."),

    // Reaction errors
    INVALID_REACTION_TYPE(HttpStatus.BAD_REQUEST, "R001", "유효하지 않은 반응 타입입니다."),
    REACTION_ALREADY_EXISTS(HttpStatus.CONFLICT, "R002", "이미 반응을 남겼습니다."),
    REACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "취소할 반응을 찾을 수 없습니다."),
    FORBIDDEN_REACTION_MODIFICATION(HttpStatus.FORBIDDEN, "R004", "이 반응을 수정/삭제할 권한이 없습니다."),

    // Notification errors

    // AI errors
    AIFEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "AI001", "AIfeedback을 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
