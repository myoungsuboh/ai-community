package com.aicommunity.common.error;

import lombok.Getter;

/**
 * 도메인 비즈니스 규칙 위반을 표현하는 예외. GlobalExceptionHandler 가 잡아 표준 응답으로 변환한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
