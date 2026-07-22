package com.aicommunity.auth.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 인증 통합 테스트 — 실제 스프링 컨텍스트 + 인메모리 H2 (Flyway 스키마) 로 HTTP 흐름 검증.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void register_then_login_flow() throws Exception {
        String email = "it-user@ai.community";
        String registerBody = "{\"email\":\"" + email + "\",\"password\":\"password123\",\"nickname\":\"통합\"}";

        // 회원가입 → 201 + refresh 쿠키 + accessToken
        MvcResult reg = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.user.role").value("MEMBER"))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andReturn();
        assertThat(reg.getResponse().getCookie("refresh_token").getValue()).isNotBlank();

        // 로그인 → 200
        String loginBody = "{\"email\":\"" + email + "\",\"password\":\"password123\"}";
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        // 잘못된 비밀번호 → 401
        String badBody = "{\"email\":\"" + email + "\",\"password\":\"WRONGpw\"}";
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(badBody))
                .andExpect(status().isUnauthorized());

        // 검증 실패(짧은 비밀번호) → 400
        String shortPw = "{\"email\":\"x@ai.community\",\"password\":\"123\",\"nickname\":\"짧\"}";
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(shortPw))
                .andExpect(status().isBadRequest());
    }
}
