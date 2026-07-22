package com.aicommunity.common.security;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * 인메모리 리프레시 토큰 저장소 (Docker 미사용 로컬 실행용).
 * 단일 인스턴스 개발 환경 기준. 다중 인스턴스/운영은 Phase 8 의 Redis 구현으로 대체.
 */
@Component
public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    private record Family(UUID userId, String currentJti, Instant expiresAt) {
    }

    private final Map<String, Family> families = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> userFamilies = new ConcurrentHashMap<>();
    private final Clock clock;

    public InMemoryRefreshTokenStore(Clock clock) {
        this.clock = clock;
    }

    @Override
    public synchronized void register(String familyId, String jti, UUID userId, Instant expiresAt) {
        families.put(familyId, new Family(userId, jti, expiresAt));
        userFamilies.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(familyId);
    }

    @Override
    public synchronized RefreshResult rotate(String familyId, String presentedJti, String newJti, Instant newExpiresAt) {
        Family f = families.get(familyId);
        if (f == null) {
            return RefreshResult.INVALID;
        }
        if (f.expiresAt().isBefore(clock.instant())) {
            removeFamily(familyId, f.userId());
            return RefreshResult.INVALID;
        }
        if (!f.currentJti().equals(presentedJti)) {
            // 이미 회전된(폐기된) jti 재사용 → 도난 신호. 패밀리 전체 폐기.
            removeFamily(familyId, f.userId());
            return RefreshResult.REUSE_DETECTED;
        }
        families.put(familyId, new Family(f.userId(), newJti, newExpiresAt));
        return RefreshResult.ROTATED;
    }

    @Override
    public synchronized void revokeFamily(String familyId) {
        Family f = families.remove(familyId);
        if (f != null) {
            Set<String> set = userFamilies.get(f.userId());
            if (set != null) {
                set.remove(familyId);
            }
        }
    }

    @Override
    public synchronized void revokeAllForUser(UUID userId) {
        Set<String> set = userFamilies.remove(userId);
        if (set != null) {
            set.forEach(families::remove);
        }
    }

    private void removeFamily(String familyId, UUID userId) {
        families.remove(familyId);
        Set<String> set = userFamilies.get(userId);
        if (set != null) {
            set.remove(familyId);
        }
    }
}
