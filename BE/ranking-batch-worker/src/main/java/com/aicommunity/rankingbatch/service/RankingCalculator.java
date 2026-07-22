package com.aicommunity.rankingbatch.service;

import com.aicommunity.common.domain.RankingEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * 주간 랭킹 산정 (2_ddd.md RankingSnapshot 불변식):
 *  점수 = 좋아요×1 + 북마크×2 + 댓글×1 + 실전점수÷10
 *  정렬 = 점수 내림차순, 동점이면 북마크 수 많은 카드 우선 (POL-19)
 */
public final class RankingCalculator {

    private RankingCalculator() {
    }

    public record CardInput(UUID cardId, String slug, String title, String category,
                            int scoreTotal, int likeCount, int bookmarkCount, int commentCount) {
    }

    public static double score(CardInput c) {
        return c.likeCount() * 1.0 + c.bookmarkCount() * 2.0 + c.commentCount() * 1.0 + c.scoreTotal() / 10.0;
    }

    public static List<RankingEntry> rank(List<CardInput> cards) {
        List<CardInput> sorted = new ArrayList<>(cards);
        sorted.sort(Comparator
                .comparingDouble(RankingCalculator::score).reversed()
                .thenComparing(Comparator.comparingInt(CardInput::bookmarkCount).reversed()));

        List<RankingEntry> entries = new ArrayList<>(sorted.size());
        int rank = 1;
        for (CardInput c : sorted) {
            entries.add(new RankingEntry(rank++, c.cardId(), c.slug(), c.title(), c.category(),
                    Math.round(score(c) * 100.0) / 100.0,
                    c.likeCount(), c.bookmarkCount(), c.commentCount(), c.scoreTotal()));
        }
        return entries;
    }
}
