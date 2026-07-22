package com.aicommunity.common.observability;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 보안 감사 로깅 (skills/security/audit_log-audit-logging.md).
 * 전용 로거("SECURITY_AUDIT")로 인증/인가 이벤트를 구조화 기록한다 — append-only(로그),
 * 항목: 시각(UTC, logback)·행위자(userId)·행위·결과. PII(이메일/비번/토큰)는 기록하지 않는다.
 */
@Component
public class SecurityAuditLogger {

    private static final Logger AUDIT = LoggerFactory.getLogger("SECURITY_AUDIT");

    public void authSuccess(String action, UUID userId) {
        AUDIT.info("event={} result=SUCCESS userId={}", action, userId);
    }

    public void authFailure(String action, String reason) {
        // 실패 시 이메일 등 PII 대신 사유 코드만 남긴다.
        AUDIT.warn("event={} result=FAILURE reason={}", action, reason);
    }

    public void accountLocked(UUID userId) {
        AUDIT.warn("event=ACCOUNT_LOCKED result=LOCKED userId={}", userId);
    }

    public void authorizationDenied(String path, String userId) {
        AUDIT.warn("event=AUTHZ_DENIED path={} userId={}", path, userId);
    }
}
