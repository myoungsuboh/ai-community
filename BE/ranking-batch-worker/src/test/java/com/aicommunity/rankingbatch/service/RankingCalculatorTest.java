package com.aicommunity.rankingbatch.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.aicommunity.common.domain.RankingEntry;
import com.aicommunity.rankingbatch.service.RankingCalculator.CardInput;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RankingCalculatorTest {

    private CardInput card(String title, int total, int like, int bookmark, int comment) {
        return new CardInput(UUID.randomUUID(), title.toLowerCase(), title, "Agent", total, like, bookmark, comment);
    }

    @Test
    @DisplayName("점수 = 좋아요×1 + 북마크×2 + 댓글×1 + 실전점수÷10")
    void score_formula() {
        // 10 + 5*2 + 3 + 80/10 = 10 + 10 + 3 + 8 = 31
        double s = RankingCalculator.score(card("A", 80, 10, 5, 3));
        assertThat(s).isEqualTo(31.0);
    }

    @Test
    @DisplayName("점수 내림차순 정렬 + rank 부여")
    void ranks_byScoreDesc() {
        List<RankingEntry> r = RankingCalculator.rank(List.of(
                card("Low", 0, 1, 0, 0),     // 1
                card("High", 100, 20, 10, 5) // 20+20+5+10 = 55
        ));
        assertThat(r.get(0).title()).isEqualTo("High");
        assertThat(r.get(0).rank()).isEqualTo(1);
        assertThat(r.get(1).title()).isEqualTo("Low");
        assertThat(r.get(1).rank()).isEqualTo(2);
    }

    @Test
    @DisplayName("동점이면 북마크 많은 카드가 상위 (POL-19)")
    void tieBreak_byBookmark() {
        // 둘 다 점수 동일해지도록 구성: A) like10,bm0 → 10 ; B) like8,bm1 → 8+2=10
        List<RankingEntry> r = RankingCalculator.rank(List.of(
                card("A", 0, 10, 0, 0),
                card("B", 0, 8, 1, 0)
        ));
        assertThat(RankingCalculator.score(new CardInput(null, null, "A", null, 0, 10, 0, 0)))
                .isEqualTo(RankingCalculator.score(new CardInput(null, null, "B", null, 0, 8, 1, 0)));
        assertThat(r.get(0).title()).isEqualTo("B"); // 북마크 1 > 0
    }
}
