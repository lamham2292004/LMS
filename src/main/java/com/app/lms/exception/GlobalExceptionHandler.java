package com.app.lms.exception;

import com.app.lms.dto.request.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErroCode errorCode = exception.getErroCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception){
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErroCode errorCode = ErroCode.INVALID_KEY;
        try {
            errorCode = ErroCode.valueOf(enumKey);
        } catch (IllegalArgumentException e){
            // giữ nguyên INVALID_KEY
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(400);
        response.setMessage("JSON không hợp lệ: " + ex.getMostSpecificCause().getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // cuối cùng mới bắt tất cả exception còn lại
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErroCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErroCode.UNCATEGORIZED_EXCEPTION.getMessage());

        // log lỗi để debug
        exception.printStackTrace();

        return ResponseEntity.badRequest().body(apiResponse);
    }
}

