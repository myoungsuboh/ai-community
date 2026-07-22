package com.aicommunity.auth.dto;

import com.aicommunity.auth.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** 인증 API 요청/응답 DTO 모음. 검증: skills/backEnd/validation-validation-bean.md */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @Email(message = "올바른 이메일 형식이 아니에요.") @NotBlank(message = "이메일을 입력해 주세요.") String email,
            @NotBlank(message = "비밀번호를 입력해 주세요.") @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.") String password,
            @NotBlank(message = "닉네임을 입력해 주세요.") @Size(max = 50) String nickname) {
    }

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password) {
    }

    public record UserSummary(UUID id, String email, String nickname, String role) {
        public static UserSummary from(User u) {
            return new UserSummary(u.getId(), u.getEmail(), u.getNickname(), u.getRole().name());
        }
    }

    /** 로그인/회원가입/갱신 응답. accessToken 은 body, refreshToken 은 HttpOnly 쿠키(응답에 없음). */
    public record AuthResponse(String accessToken, String tokenType, UserSummary user) {
        public static AuthResponse of(String accessToken, UserSummary user) {
            return new AuthResponse(accessToken, "Bearer", user);
        }
    }
}
