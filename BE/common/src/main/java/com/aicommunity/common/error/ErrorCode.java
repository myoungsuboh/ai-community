package com.aicommunity.common.error;

import org.springframework.http.HttpStatus;

/**
 * 도메인 공용 에러 코드. 각 서비스는 필요한 코드를 여기서 사용한다.
 * 메시지는 사용자 친화적 한국어 (spec 의 에러 메시지 규약 반영).
 */
public enum ErrorCode {

    // 공통
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    UNPROCESSABLE(HttpStatus.UNPROCESSABLE_ENTITY, "요청을 처리할 수 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "이미 존재하거나 충돌하는 요청입니다."),
    RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다."),

    // 인증/사용자 (Phase 2)
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    ACCOUNT_LOCKED(HttpStatus.LOCKED, "로그인 5회 실패로 계정이 잠겼습니다. 잠시 후 다시 시도해 주세요."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "인증 정보가 유효하지 않습니다."),

    // 콘텐츠/큐레이션 (Phase 3)
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 카드를 찾을 수 없습니다."),
    INVALID_URL(HttpStatus.UNPROCESSABLE_ENTITY, "올바른 URL 형식이 아닙니다."),
    DUPLICATE_SUBMISSION(HttpStatus.CONFLICT, "이미 제보되었거나 검수 중인 URL 입니다."),
    SUBMISSION_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "내일 다시 제보할 수 있어요."),
    SCORE_MISMATCH(HttpStatus.UNPROCESSABLE_ENTITY, "4축 합계와 총점이 일치하지 않습니다."),
    REJECTION_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "반려 사유는 필수입니다."),
    SCORE_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "실전점수 변경 사유는 필수입니다."),

    // 사용자 활동 (Phase 4)
    COMMENT_RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "댓글은 분당 3건까지 작성할 수 있어요."),

    // 프로젝트 (Phase 6)
    PROJECT_NAME_DUPLICATE(HttpStatus.CONFLICT, "이미 존재하는 프로젝트명입니다.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() {
        return status;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
