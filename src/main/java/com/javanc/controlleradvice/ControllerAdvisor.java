package com.javanc.controlleradvice;

import com.javanc.controlleradvice.customeException.AppException;
import com.javanc.controlleradvice.customeException.UserNotExistsException;
import com.javanc.enums.ErrorCode;
import com.javanc.model.response.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleAllExceptions(RuntimeException ex, WebRequest request) {
        ApiResponseDTO<Void> apiResponseDTO = ApiResponseDTO.<Void>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponseDTO);
    }

    @ExceptionHandler(UserNotExistsException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleUserNotFound(UserNotExistsException ex, WebRequest request) {
        ApiResponseDTO<Void> apiResponseDTO = ApiResponseDTO.<Void>builder()
                .code(ErrorCode.USER_NOT_EXISTS.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleUserNotExistsException(AppException ex, WebRequest request){
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponseDTO<Void> apiResponseDTO = ApiResponseDTO.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponseDTO);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleAccessDenied(AccessDeniedException ex, WebRequest request){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ApiResponseDTO.<Void>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }
}
