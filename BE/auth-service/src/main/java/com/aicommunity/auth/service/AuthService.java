package com.aicommunity.auth.service;

import com.aicommunity.auth.domain.User;
import com.aicommunity.auth.domain.UserRepository;
import com.aicommunity.auth.domain.event.UserEvents;
import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.common.security.JwtTokenProvider;
import com.aicommunity.common.security.RefreshTokenStore;
import com.aicommunity.common.security.Role;
import com.aicommunity.common.security.UserPrincipal;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스 비즈니스 로직. 회원가입/로그인/토큰 갱신(회전)/로그아웃.
 * 정책: POL-37(5회 실패 10분 잠금), POL-39(8자↑는 DTO 검증), POL-40(세션 7일).
 */
@Service
public class AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(10);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider, RefreshTokenStore refreshTokenStore,
                       ApplicationEventPublisher events, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenStore = refreshTokenStore;
        this.events = events;
        this.clock = clock;
    }

    public record IssuedTokens(String accessToken, String refreshToken, User user) {
    }

    @Transactional
    public IssuedTokens register(String email, String rawPassword, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = User.create(email, passwordEncoder.encode(rawPassword), nickname, Role.MEMBER);
        user.onSuccessfulLogin(tokenProvider.refreshTtl(), clock); // 가입 즉시 세션 시작
        userRepository.save(user);
        events.publishEvent(new UserEvents.UserRegistered(
                user.getId(), user.getEmail(), user.getNickname(), OffsetDateTime.now(clock)));
        return issueTokens(user);
    }

    // noRollbackFor: 로그인 실패 시 실패 카운트/잠금 증가를 커밋해야 하므로 BusinessException 에 롤백하지 않는다.
    @Transactional(noRollbackFor = BusinessException.class)
    public IssuedTokens login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (user.isLocked(clock)) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            boolean lockedNow = user.registerFailedLogin(MAX_LOGIN_ATTEMPTS, LOCK_DURATION, clock);
            userRepository.save(user);
            if (lockedNow) {
                events.publishEvent(new UserEvents.UserAccountLocked(
                        user.getId(), user.getLockedUntil(), OffsetDateTime.now(clock)));
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
            }
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.onSuccessfulLogin(tokenProvider.refreshTtl(), clock);
        userRepository.save(user);
        events.publishEvent(new UserEvents.UserLoggedIn(user.getId(), OffsetDateTime.now(clock)));
        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public IssuedTokens refresh(String refreshTokenRaw) {
        JwtTokenProvider.RefreshClaims claims;
        try {
            claims = tokenProvider.parseRefreshToken(refreshTokenRaw);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        String newJti = UUID.randomUUID().toString();
        OffsetDateTime newExpiry = OffsetDateTime.now(clock).plus(tokenProvider.refreshTtl());
        RefreshTokenStore.RefreshResult result =
                refreshTokenStore.rotate(claims.familyId(), claims.jti(), newJti, newExpiry.toInstant());

        if (result != RefreshTokenStore.RefreshResult.ROTATED) {
            // REUSE_DETECTED(도난)·INVALID 모두 갱신 거부. 재사용은 store 가 패밀리를 이미 폐기함.
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(claims.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
        String access = tokenProvider.createAccessToken(toPrincipal(user));
        String refresh = tokenProvider.createRefreshToken(user.getId(), claims.familyId(), newJti);
        return new IssuedTokens(access, refresh, user);
    }

    public void logout(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
            return;
        }
        try {
            JwtTokenProvider.RefreshClaims claims = tokenProvider.parseRefreshToken(refreshTokenRaw);
            refreshTokenStore.revokeFamily(claims.familyId());
        } catch (Exception ignored) {
            // 이미 무효/만료 → 로그아웃은 멱등적으로 성공 처리
        }
    }

    private IssuedTokens issueTokens(User user) {
        String familyId = UUID.randomUUID().toString();
        String jti = UUID.randomUUID().toString();
        OffsetDateTime expiry = OffsetDateTime.now(clock).plus(tokenProvider.refreshTtl());
        refreshTokenStore.register(familyId, jti, user.getId(), expiry.toInstant());
        String access = tokenProvider.createAccessToken(toPrincipal(user));
        String refresh = tokenProvider.createRefreshToken(user.getId(), familyId, jti);
        return new IssuedTokens(access, refresh, user);
    }

    private UserPrincipal toPrincipal(User user) {
        return new UserPrincipal(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }
}
