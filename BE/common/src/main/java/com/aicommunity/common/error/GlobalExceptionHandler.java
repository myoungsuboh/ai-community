package com.aicommunity.common.error;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 → 표준 ErrorResponse 변환. 모든 서비스가 공유(component scan).
 * skills/core/error-handling-error-handling-resilience.md
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        ErrorCode code = ex.getErrorCode();
        return ResponseEntity.status(code.status())
                .body(ErrorResponse.of(code.status().value(), code.name(), ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();
        ErrorCode code = ErrorCode.VALIDATION_FAILED;
        return ResponseEntity.status(code.status())
                .body(ErrorResponse.of(code.status().value(), code.name(), code.defaultMessage(),
                        req.getRequestURI(), fieldErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity.status(code.status())
                .body(ErrorResponse.of(code.status().value(), code.name(), code.defaultMessage(), req.getRequestURI()));
    }

    private ErrorResponse.FieldError toFieldError(FieldError fe) {
        return new ErrorResponse.FieldError(fe.getField(),
                fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage());
    }
}
