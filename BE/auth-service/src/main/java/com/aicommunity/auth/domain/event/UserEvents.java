package com.aicommunity.auth.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * User 애그리거트 도메인 이벤트 (2_ddd.md EVT-16/17/18).
 * 인프로세스(Spring ApplicationEvent)로 발행 — Architecture 에 메시지 브로커 미지정.
 */
public final class UserEvents {

    private UserEvents() {
    }

    public record UserRegistered(UUID userId, String email, String nickname, OffsetDateTime registeredAt) {
    }

    public record UserLoggedIn(UUID userId, OffsetDateTime loggedInAt) {
    }

    public record UserAccountLocked(UUID userId, OffsetDateTime lockedUntil, OffsetDateTime lockedAt) {
    }
}
