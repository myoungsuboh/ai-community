package com.aicommunity.common.error;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 모든 서비스가 공유하는 표준 에러 응답 형식.
 * skills/backEnd/REST-api-design-principles.md — 일관된 에러 포맷.
 */
public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldError> errors
) {
    public record FieldError(String field, String reason) {
    }

    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(OffsetDateTime.now(), status, code, message, path, List.of());
    }

    public static ErrorResponse of(int status, String code, String message, String path, List<FieldError> errors) {
        return new ErrorResponse(OffsetDateTime.now(), status, code, message, path, errors);
    }
}
