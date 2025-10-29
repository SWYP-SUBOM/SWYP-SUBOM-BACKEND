package swyp_11.ssubom.global.error;

import org.springframework.validation.BindingResult;
import swyp_11.ssubom.global.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.global.response.ErrorResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import java.util.stream.Collectors;

// 모든 controller들을 위한 global handler
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. businessException 처리 예정
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage(), e);
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return build(errorCode, errorResponse);
    }

    // 2. @Valid 유효성 검사 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        BindingResult bindingResult = e.getBindingResult();

        List<ErrorResponse.FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.FieldError(
                        fieldError.getField(),
                        fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue().toString(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, fieldErrors);
        return build(errorCode, errorResponse);
    }

    // 3. 401 (인증) 에러 처리 (Spring Security)
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException e) {
        log.warn("AuthenticationException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_REQUIRED;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return build(errorCode, errorResponse);
    }

    // 4. 403 (권한) 에러 처리 (Spring Security)
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("AccessDeniedException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return build(errorCode, errorResponse);
    }

    // 5. 405 (HTTP Method) 에러 처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("HttpRequestMethodNotSupportedException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return build(errorCode, errorResponse);
    }

    // 6. 400 (JSON 형식) 에러 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_JSON;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return build(errorCode, errorResponse);
    }

    // 7. 그 외 모든 예외 처리 (최후의 보루)
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("UnhandledException: {}", e.getMessage(), e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return build(errorCode, errorResponse);
    }


    private ResponseEntity<ApiResponse<?>> build(ErrorCode errorCode, ErrorResponse errorResponse) {
        ApiResponse<?> apiResponse = ApiResponse.error(errorResponse);
        return new ResponseEntity<>(apiResponse, errorCode.getHttpStatus());
    }
}

