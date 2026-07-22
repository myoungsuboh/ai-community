package com.aicommunity.rankingbatch;

import static org.assertj.core.api.Assertions.assertThat;

import com.aicommunity.common.domain.RankingEntry;
import com.aicommunity.rankingbatch.domain.RankingSnapshot;
import com.aicommunity.rankingbatch.service.RankingBatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * 배치가 발행 7일 경과 카드만(POL-23) 산정해 스냅샷을 생성하는지 검증.
 */
@SpringBootTest
@ActiveProfiles("test")
class RankingBatchIntegrationTest {

    @Autowired RankingBatchService batchService;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper objectMapper;

    private void insertCard(String slug, String title, int total, int like, int bookmark, int comment,
                            OffsetDateTime publishedAt) {
        jdbc.update("""
                INSERT INTO cards (id, slug, title, category, repo_url, status, source_accessible,
                                   published_at, score_total, like_count, bookmark_count, comment_count)
                VALUES (?,?,?,?,?, 'PUBLISHED', TRUE, ?,?,?,?,?)
                """,
                UUID.randomUUID(), slug, title, "Agent", "https://x.y/" + slug,
                publishedAt, total, like, bookmark, comment);
    }

    @Test
    void run_ranksEligibleCardsOnly() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        // 발행 10일 경과 (대상)
        insertCard("old-top", "오래된 인기카드", 80, 20, 10, 5, now.minusDays(10)); // 20+20+5+8=53
        insertCard("old-low", "오래된 저점카드", 10, 1, 0, 0, now.minusDays(9));    // 1+0+0+1=2
        // 발행 1일 (POL-23: 제외)
        insertCard("too-new", "새 카드", 100, 99, 99, 99, now.minusDays(1));

        RankingSnapshot snapshot = batchService.run();

        List<RankingEntry> entries = objectMapper.readValue(snapshot.getEntries(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, RankingEntry.class));

        assertThat(entries).hasSize(2); // too-new 제외
        assertThat(entries).extracting(RankingEntry::slug).doesNotContain("too-new");
        assertThat(entries.get(0).slug()).isEqualTo("old-top"); // 최고 점수 1위
        assertThat(entries.get(0).rank()).isEqualTo(1);
    }
}
