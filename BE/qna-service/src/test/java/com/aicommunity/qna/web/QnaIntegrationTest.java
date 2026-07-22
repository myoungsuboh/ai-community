package com.aicommunity.qna.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicommunity.common.security.JwtTokenProvider;
import com.aicommunity.common.security.Role;
import com.aicommunity.common.security.UserPrincipal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QnaIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtTokenProvider tokenProvider;

    private String bearer() {
        return "Bearer " + tokenProvider.createAccessToken(
                new UserPrincipal(UUID.randomUUID(), "u@ai.community", "회원", Role.MEMBER));
    }

    @Test
    void list_isPublic() throws Exception {
        mockMvc.perform(get("/api/v1/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void createQuestion_requiresAuth() throws Exception {
        mockMvc.perform(post("/api/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"질문\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void emptyContent_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/questions")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuestion_thenAnswer_flow() throws Exception {
        String auth = bearer();
        String qBody = "{\"title\":\"RAG 질문\",\"content\":\"청킹 전략 추천?\"}";
        String location = mockMvc.perform(post("/api/v1/questions")
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content(qBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String questionId = location.replaceAll(".*\"questionId\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/v1/questions/{id}/answers", questionId)
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"고정 크기 청킹부터\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/questions/{id}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answerCount").value(1))
                .andExpect(jsonPath("$.answers[0].content").value("고정 크기 청킹부터"));
    }
}
