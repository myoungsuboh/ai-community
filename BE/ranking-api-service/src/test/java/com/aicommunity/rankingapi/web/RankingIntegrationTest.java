package com.aicommunity.rankingapi.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RankingIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbc;

    @Test
    void weekly_isPublic_andEmptyWhenNoSnapshot() throws Exception {
        mockMvc.perform(get("/api/v1/rankings/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries").isArray());
    }

    @Test
    void weekly_returnsLatestSnapshotEntries() throws Exception {
        String entries = "[{\"rank\":1,\"cardId\":\"" + UUID.randomUUID()
                + "\",\"slug\":\"autogen\",\"title\":\"AutoGen\",\"category\":\"Agent\","
                + "\"score\":53.0,\"likeCount\":20,\"bookmarkCount\":10,\"commentCount\":5,\"scoreTotal\":80}]";
        jdbc.update("""
                INSERT INTO ranking_snapshots (id, snapshot_year, week_of_year, entries, generated_at)
                VALUES (?, 2026, 30, ?, CURRENT_TIMESTAMP)
                """, UUID.randomUUID(), entries);

        mockMvc.perform(get("/api/v1/rankings/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2026))
                .andExpect(jsonPath("$.week").value(30))
                .andExpect(jsonPath("$.entries[0].slug").value("autogen"))
                .andExpect(jsonPath("$.entries[0].rank").value(1));
    }
}
