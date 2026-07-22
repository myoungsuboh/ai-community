package com.aicommunity.auth.domain;

import com.aicommunity.common.entity.BaseTimeEntity;
import com.aicommunity.common.security.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User 애그리거트 (인증 서비스 소유).
 * 불변식(2_ddd.md): 비밀번호 8자↑(가입 전 검증), 5회 실패 시 10분 잠금, 세션 7일, 닉네임 필수.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;

    @Column(name = "session_expires_at")
    private OffsetDateTime sessionExpiresAt;

    public static User create(String email, String passwordHash, String nickname, Role role) {
        User u = new User();
        u.id = UUID.randomUUID();
        u.email = email;
        u.passwordHash = passwordHash;
        u.nickname = nickname;
        u.role = role;
        u.failedLoginCount = 0;
        return u;
    }

    public boolean isLocked(Clock clock) {
        return lockedUntil != null && lockedUntil.isAfter(OffsetDateTime.now(clock));
    }

    /** 로그인 실패 기록. 임계치 도달 시 잠금. 잠금이 새로 걸렸으면 true. */
    public boolean registerFailedLogin(int maxAttempts, Duration lockDuration, Clock clock) {
        this.failedLoginCount++;
        if (this.failedLoginCount >= maxAttempts) {
            this.lockedUntil = OffsetDateTime.now(clock).plus(lockDuration);
            return true;
        }
        return false;
    }

    /** 로그인 성공: 실패 카운트/잠금 해제 + 세션 만료 갱신. */
    public void onSuccessfulLogin(Duration sessionTtl, Clock clock) {
        this.failedLoginCount = 0;
        this.lockedUntil = null;
        this.sessionExpiresAt = OffsetDateTime.now(clock).plus(sessionTtl);
    }
}
