package swyp_11.ssubom.global.error;

import swyp_11.ssubom.global.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.global.response.ErrorResponse;

// 모든 controller들을 위한 global handler
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        ApiResponse<?> apiResponse = ApiResponse.error(errorResponse);

        return new ResponseEntity<>(apiResponse, errorCode.getHttpStatus());
    }

    // Handles @Valid annotation failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());

        String firstErrorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getErrorCode(), firstErrorMessage);
        ApiResponse<?> apiResponse = ApiResponse.error(errorResponse);

        return new ResponseEntity<>(apiResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("UnhandledException: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        ApiResponse<?> apiResponse = ApiResponse.error(errorResponse);

        return new ResponseEntity<>(apiResponse, errorCode.getHttpStatus());
    }
}

