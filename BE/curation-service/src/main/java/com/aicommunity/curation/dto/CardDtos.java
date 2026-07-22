package com.aicommunity.curation.dto;

import com.aicommunity.curation.domain.Card;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class CardDtos {

    private CardDtos() {
    }

    /** 발행 카드 수정 요청. 실전점수(축) 변경 시 reason 필수(POL-10). */
    public record UpdateCardRequest(
            String title,
            String category,
            String summaryLine1,
            String summaryLine2,
            String summaryLine3,
            Integer scoreAxisDocs,
            Integer scoreAxisActivity,
            Integer scoreAxisPopularity,
            Integer scoreAxisMaintenance,
            String reason) {
    }

    public record CardResponse(UUID cardId, String slug, String title, String category,
                               int scoreTotal, String status, OffsetDateTime updatedAt) {
        public static CardResponse from(Card c) {
            return new CardResponse(c.getId(), c.getSlug(), c.getTitle(), c.getCategory(),
                    c.getScoreTotal(), c.getStatus().name(), c.getUpdatedAt());
        }
    }
}
