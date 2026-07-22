package com.aicommunity.curation.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class CurationIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void posts_listIsPublic() throws Exception {
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void submit_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://github.com/x/y\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void submissionList_withoutAuth_isDenied() throws Exception {
        mockMvc.perform(get("/api/v1/submissions").param("status", "PENDING"))
                .andExpect(status().isUnauthorized());
    }
}
