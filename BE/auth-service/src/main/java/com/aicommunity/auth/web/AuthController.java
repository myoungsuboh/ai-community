package com.aicommunity.auth.web;

import com.aicommunity.auth.dto.AuthDtos.AuthResponse;
import com.aicommunity.auth.dto.AuthDtos.LoginRequest;
import com.aicommunity.auth.dto.AuthDtos.RegisterRequest;
import com.aicommunity.auth.dto.AuthDtos.UserSummary;
import com.aicommunity.auth.service.AuthService;
import com.aicommunity.auth.service.AuthService.IssuedTokens;
import com.aicommunity.common.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 API — 회원가입/로그인/토큰 갱신/로그아웃.
 * accessToken 은 응답 body, refreshToken 은 HttpOnly 쿠키(skills/security/jwt-refresh-rotation, routing-auth).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";
    private static final String COOKIE_PATH = "/api/v1/auth";

    private final AuthService authService;
    private final long refreshMaxAge;
    private final boolean cookieSecure;

    public AuthController(AuthService authService, JwtTokenProvider tokenProvider,
                          @Value("${auth.cookie.secure:false}") boolean cookieSecure) {
        this.authService = authService;
        this.refreshMaxAge = tokenProvider.refreshTtl().getSeconds();
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        IssuedTokens tokens = authService.register(req.email(), req.password(), req.nickname());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie(tokens.refreshToken()).toString())
                .body(body(tokens));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        IssuedTokens tokens = authService.login(req.email(), req.password());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(tokens.refreshToken()).toString())
                .body(body(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        IssuedTokens tokens = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(tokens.refreshToken()).toString())
                .body(body(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie().toString())
                .build();
    }

    private AuthResponse body(IssuedTokens tokens) {
        return AuthResponse.of(tokens.accessToken(), UserSummary.from(tokens.user()));
    }

    private ResponseCookie refreshCookie(String value) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(refreshMaxAge)
                .build();
    }

    private ResponseCookie clearCookie() {
        return ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();
    }
}
