package com.aicommunity.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 액세스/리프레시 토큰 발급·검증.
 * skills/security/jwt-jwt-refresh-rotation.md: 짧은 access TTL, 7일 refresh, 회전용 jti/family 클레임.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final Clock clock;

    public JwtTokenProvider(
            @Value("${jwt.secret:dev-only-change-me-please-32bytes-minimum-secret-key}") String secret,
            @Value("${jwt.access-ttl-seconds:1800}") long accessTtlSeconds,
            @Value("${jwt.refresh-ttl-seconds:604800}") long refreshTtlSeconds,
            Clock clock) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.clock = clock;
    }

    public Duration refreshTtl() {
        return Duration.ofSeconds(refreshTtlSeconds);
    }

    public String createAccessToken(UserPrincipal user) {
        Date now = Date.from(clock.instant());
        Date exp = Date.from(clock.instant().plusSeconds(accessTtlSeconds));
        return Jwts.builder()
                .subject(user.id().toString())
                .claim("type", "access")
                .claim("email", user.email())
                .claim("nickname", user.nickname())
                .claim("role", user.role().name())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(UUID userId, String familyId, String jti) {
        Date now = Date.from(clock.instant());
        Date exp = Date.from(clock.instant().plusSeconds(refreshTtlSeconds));
        return Jwts.builder()
                .subject(userId.toString())
                .id(jti)
                .claim("type", "refresh")
                .claim("fam", familyId)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    /** 액세스 토큰 파싱 → 인증 주체. 서명/만료/타입 불일치 시 예외. */
    public UserPrincipal parseAccessToken(String token) {
        Claims c = parse(token);
        if (!"access".equals(c.get("type", String.class))) {
            throw new IllegalArgumentException("not an access token");
        }
        return new UserPrincipal(
                UUID.fromString(c.getSubject()),
                c.get("email", String.class),
                c.get("nickname", String.class),
                Role.valueOf(c.get("role", String.class)));
    }

    public RefreshClaims parseRefreshToken(String token) {
        Claims c = parse(token);
        if (!"refresh".equals(c.get("type", String.class))) {
            throw new IllegalArgumentException("not a refresh token");
        }
        return new RefreshClaims(
                UUID.fromString(c.getSubject()),
                c.get("fam", String.class),
                c.getId());
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public record RefreshClaims(UUID userId, String familyId, String jti) {
    }
}
