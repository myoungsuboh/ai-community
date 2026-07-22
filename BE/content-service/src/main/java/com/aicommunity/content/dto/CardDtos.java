package com.aicommunity.content.dto;

import com.aicommunity.content.domain.Card;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class CardDtos {

    private CardDtos() {
    }

    /** 홈 피드 카드 그리드 항목. */
    public record CardSummary(UUID cardId, String slug, String title, String category,
                              int scoreTotal, int starCount, int likeCount, int bookmarkCount,
                              int commentCount, boolean sourceAccessible, OffsetDateTime publishedAt) {
        public static CardSummary from(Card c) {
            return new CardSummary(c.getId(), c.getSlug(), c.getTitle(), c.getCategory(),
                    c.getScoreTotal(), c.getStarCount(), c.getLikeCount(), c.getBookmarkCount(),
                    c.getCommentCount(), c.isSourceAccessible(), c.getPublishedAt());
        }
    }

    /** 카드 상세: 요약 전문 + 4축 점수 + 커뮤니티 반응. */
    public record CardDetail(UUID cardId, String slug, String title, String category, String repoUrl,
                             String summaryLine1, String summaryLine2, String summaryLine3,
                             int scoreTotal, int scoreAxisDocs, int scoreAxisActivity,
                             int scoreAxisPopularity, int scoreAxisMaintenance,
                             int starCount, int likeCount, int bookmarkCount, int commentCount,
                             boolean sourceAccessible, OffsetDateTime metaUpdatedAt, OffsetDateTime publishedAt) {
        public static CardDetail from(Card c) {
            return new CardDetail(c.getId(), c.getSlug(), c.getTitle(), c.getCategory(), c.getRepoUrl(),
                    c.getSummaryLine1(), c.getSummaryLine2(), c.getSummaryLine3(),
                    c.getScoreTotal(), c.getScoreAxisDocs(), c.getScoreAxisActivity(),
                    c.getScoreAxisPopularity(), c.getScoreAxisMaintenance(),
                    c.getStarCount(), c.getLikeCount(), c.getBookmarkCount(), c.getCommentCount(),
                    c.isSourceAccessible(), c.getMetaUpdatedAt(), c.getPublishedAt());
        }
    }
}
