package com.aicommunity.common.security;

import java.time.Instant;
import java.util.UUID;

/**
 * 리프레시 토큰 패밀리 저장소 (skills/security/jwt-jwt-refresh-rotation.md).
 * 회전(rotate)·재사용 탐지(REUSE_DETECTED)·패밀리 무효화(revoke)를 서버측에서 강제한다.
 * 원문 토큰이 아니라 식별자(jti/familyId)만 저장한다.
 *
 * 구현: local/기본 = InMemoryRefreshTokenStore. Redis 기반 구현은 Phase 8(cross-cutting)에서 추가.
 */
public interface RefreshTokenStore {

    enum RefreshResult { ROTATED, REUSE_DETECTED, INVALID }

    /** 로그인 시 새 패밀리 등록. */
    void register(String familyId, String jti, UUID userId, Instant expiresAt);

    /** 회전 시도. 성공 시 ROTATED(newJti 저장), 폐기된 jti 재사용 시 REUSE_DETECTED(패밀리 폐기), 그 외 INVALID. */
    RefreshResult rotate(String familyId, String presentedJti, String newJti, Instant newExpiresAt);

    /** 특정 패밀리 폐기 (로그아웃). */
    void revokeFamily(String familyId);

    /** 사용자의 모든 패밀리 폐기 (비밀번호 변경·전체 로그아웃·도난 대응). */
    void revokeAllForUser(UUID userId);
}
