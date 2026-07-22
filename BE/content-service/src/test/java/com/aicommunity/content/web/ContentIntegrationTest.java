package com.aicommunity.content.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContentIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void feed_isPublicAndReturnsPage() throws Exception {
        mockMvc.perform(get("/api/v1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void feed_invalidMinScore_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/cards").param("minScore", "999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detail_unknownSlug_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/cards/does-not-exist"))
                .andExpect(status().isNotFound());
    }
}
