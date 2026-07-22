package com.aicommunity.project.web;

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
class ProjectIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtTokenProvider tokenProvider;

    private String bearer() {
        return "Bearer " + tokenProvider.createAccessToken(
                new UserPrincipal(UUID.randomUUID(), "u@ai.community", "회원", Role.MEMBER));
    }

    @Test
    void list_isPublic() throws Exception {
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void create_requiresAuth() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"프로젝트\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shortName_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_thenDetail() throws Exception {
        String body = mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"멀티에이전트 스터디\",\"description\":\"주 1회\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECRUITING"))
                .andReturn().getResponse().getContentAsString();
        String id = body.replaceAll(".*\"projectId\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/v1/projects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("멀티에이전트 스터디"));
    }
}
