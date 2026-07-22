package com.aicommunity.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicommunity.auth.domain.User;
import com.aicommunity.auth.domain.UserRepository;
import com.aicommunity.auth.domain.event.UserEvents;
import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.common.security.JwtTokenProvider;
import com.aicommunity.common.security.RefreshTokenStore;
import com.aicommunity.common.security.Role;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider tokenProvider;
    @Mock RefreshTokenStore refreshTokenStore;
    @Mock ApplicationEventPublisher events;
    @Mock com.aicommunity.common.observability.SecurityAuditLogger audit;

    Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);
    AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, tokenProvider,
                refreshTokenStore, events, audit, clock);
    }

    private void stubTokenIssuance() {
        when(tokenProvider.refreshTtl()).thenReturn(Duration.ofDays(7));
        when(tokenProvider.createAccessToken(any())).thenReturn("access-token");
        when(tokenProvider.createRefreshToken(any(), anyString(), anyString())).thenReturn("refresh-token");
    }

    @Test
    @DisplayName("회원가입: 신규 이메일이면 저장하고 UserRegistered 이벤트를 발행한다")
    void register_success() {
        when(userRepository.existsByEmail("a@ai.community")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("HASHED");
        stubTokenIssuance();

        AuthService.IssuedTokens tokens = authService.register("a@ai.community", "password123", "닉");

        assertThat(tokens.accessToken()).isEqualTo("access-token");
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token");
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPasswordHash()).isEqualTo("HASHED");
        assertThat(saved.getValue().getRole()).isEqualTo(Role.MEMBER);
        verify(events).publishEvent(any(UserEvents.UserRegistered.class));
    }

    @Test
    @DisplayName("회원가입: 중복 이메일이면 EMAIL_ALREADY_EXISTS")
    void register_duplicate() {
        when(userRepository.existsByEmail("a@ai.community")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("a@ai.community", "password123", "닉"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그인: 올바른 비밀번호면 토큰 발급 + UserLoggedIn 이벤트")
    void login_success() {
        User user = User.create("a@ai.community", "HASHED", "닉", Role.MEMBER);
        when(userRepository.findByEmail("a@ai.community")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "HASHED")).thenReturn(true);
        stubTokenIssuance();

        AuthService.IssuedTokens tokens = authService.login("a@ai.community", "password123");

        assertThat(tokens.accessToken()).isEqualTo("access-token");
        verify(events).publishEvent(any(UserEvents.UserLoggedIn.class));
    }

    @Test
    @DisplayName("로그인: 틀린 비밀번호면 실패 카운트 증가 후 INVALID_CREDENTIALS")
    void login_wrongPassword_increments() {
        User user = User.create("a@ai.community", "HASHED", "닉", Role.MEMBER);
        when(userRepository.findByEmail("a@ai.community")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "HASHED")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("a@ai.community", "bad"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_CREDENTIALS);
        assertThat(user.getFailedLoginCount()).isEqualTo(1);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("로그인: 5회째 실패면 계정 잠금 + UserAccountLocked (POL-37)")
    void login_fifthFailure_locks() {
        User user = User.create("a@ai.community", "HASHED", "닉", Role.MEMBER);
        for (int i = 0; i < 4; i++) {
            user.registerFailedLogin(5, Duration.ofMinutes(10), clock); // 4회까지 누적(미잠금)
        }
        when(userRepository.findByEmail("a@ai.community")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "HASHED")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("a@ai.community", "bad"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.ACCOUNT_LOCKED);
        assertThat(user.getFailedLoginCount()).isEqualTo(5);
        assertThat(user.isLocked(clock)).isTrue();
        verify(events).publishEvent(any(UserEvents.UserAccountLocked.class));
    }

    @Test
    @DisplayName("로그인: 이미 잠긴 계정이면 ACCOUNT_LOCKED")
    void login_lockedAccount() {
        User user = User.create("a@ai.community", "HASHED", "닉", Role.MEMBER);
        for (int i = 0; i < 5; i++) {
            user.registerFailedLogin(5, Duration.ofMinutes(10), clock);
        }
        when(userRepository.findByEmail("a@ai.community")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login("a@ai.community", "password123"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.ACCOUNT_LOCKED);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("토큰 갱신: 회전 성공이면 새 토큰 발급")
    void refresh_rotates() {
        UUID userId = UUID.randomUUID();
        User user = User.create("a@ai.community", "HASHED", "닉", Role.MEMBER);
        when(tokenProvider.parseRefreshToken("raw"))
                .thenReturn(new JwtTokenProvider.RefreshClaims(userId, "fam-1", "jti-1"));
        when(tokenProvider.refreshTtl()).thenReturn(Duration.ofDays(7));
        when(refreshTokenStore.rotate(eq("fam-1"), eq("jti-1"), anyString(), any()))
                .thenReturn(RefreshTokenStore.RefreshResult.ROTATED);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenProvider.createAccessToken(any())).thenReturn("new-access");
        // createRefreshToken 의 첫 인자는 로드된 user.getId() (랜덤) 이므로 any() 로 둔다.
        when(tokenProvider.createRefreshToken(any(), eq("fam-1"), anyString())).thenReturn("new-refresh");

        AuthService.IssuedTokens tokens = authService.refresh("raw");

        assertThat(tokens.accessToken()).isEqualTo("new-access");
        assertThat(tokens.refreshToken()).isEqualTo("new-refresh");
    }

    @Test
    @DisplayName("토큰 갱신: 폐기된 토큰 재사용(REUSE_DETECTED)이면 INVALID_TOKEN")
    void refresh_reuseDetected() {
        UUID userId = UUID.randomUUID();
        when(tokenProvider.parseRefreshToken("raw"))
                .thenReturn(new JwtTokenProvider.RefreshClaims(userId, "fam-1", "old-jti"));
        when(tokenProvider.refreshTtl()).thenReturn(Duration.ofDays(7));
        when(refreshTokenStore.rotate(eq("fam-1"), eq("old-jti"), anyString(), any()))
                .thenReturn(RefreshTokenStore.RefreshResult.REUSE_DETECTED);

        assertThatThrownBy(() -> authService.refresh("raw"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_TOKEN);
        verify(userRepository, never()).findById(any());
    }
}
