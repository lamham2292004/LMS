package com.app.lms.exception;

import com.app.lms.dto.request.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Custom App Exceptions
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErroCode errorCode = exception.getErroCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        log.warn("App Exception: {} - {}", errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Validation Errors
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErroCode errorCode = ErroCode.INVALID_KEY;
        try {
            errorCode = ErroCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            // giữ nguyên INVALID_KEY
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        log.warn("Validation Error: {} - {}", errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Security Exception - Access Denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(ErroCode.ACCESS_DENIED.getCode());
        response.setMessage(ErroCode.ACCESS_DENIED.getMessage());

        log.warn("Access Denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Security Exception - Authentication
    @ExceptionHandler({AuthenticationException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<ApiResponse> handleAuthentication(Exception ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(ErroCode.TOKEN_INVALID.getCode());
        response.setMessage("Vui lòng đăng nhập để tiếp tục");

        log.warn("Authentication Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // JSON Parse Error
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(400);
        response.setMessage("JSON không hợp lệ: " + ex.getMostSpecificCause().getMessage());

        log.warn("JSON Parse Error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // File Upload Size Exceeded
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(ErroCode.FILE_ERRO.getCode());
        response.setMessage("File quá lớn. Kích thước tối đa cho phép là 100MB");

        log.warn("File Upload Size Exceeded: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // 404 Not Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NoHandlerFoundException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(404);
        response.setMessage("Endpoint không tồn tại: " + ex.getRequestURL());

        log.warn("Endpoint Not Found: {}", ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErroCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErroCode.UNCATEGORIZED_EXCEPTION.getMessage());

        // Log full stack trace for debugging
        log.error("Uncategorized Exception: ", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}

