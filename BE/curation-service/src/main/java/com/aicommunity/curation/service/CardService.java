package com.aicommunity.curation.service;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.curation.domain.AuditLog;
import com.aicommunity.curation.domain.AuditLogRepository;
import com.aicommunity.curation.domain.Card;
import com.aicommunity.curation.domain.CardRepository;
import com.aicommunity.curation.domain.event.CurationEvents;
import com.aicommunity.curation.dto.CardDtos.UpdateCardRequest;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public CardService(CardRepository cardRepository, AuditLogRepository auditLogRepository,
                       ApplicationEventPublisher events, Clock clock) {
        this.cardRepository = cardRepository;
        this.auditLogRepository = auditLogRepository;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public Card update(UUID curatorId, UUID cardId, UpdateCardRequest req) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_NOT_FOUND));

        boolean scoreChanged = card.scoreChanged(req.scoreAxisDocs(), req.scoreAxisActivity(),
                req.scoreAxisPopularity(), req.scoreAxisMaintenance());
        // POL-10: 실전점수 변경 시 사유 필수
        if (scoreChanged && (req.reason() == null || req.reason().isBlank())) {
            throw new BusinessException(ErrorCode.SCORE_REASON_REQUIRED);
        }

        card.updateInfo(req.title(), req.category(), req.summaryLine1(), req.summaryLine2(), req.summaryLine3(),
                req.scoreAxisDocs(), req.scoreAxisActivity(), req.scoreAxisPopularity(), req.scoreAxisMaintenance());
        cardRepository.save(card);

        OffsetDateTime now = OffsetDateTime.now(clock);
        String changed = buildChangedFields(req);
        auditLogRepository.save(AuditLog.of(card.getId(), curatorId, "UPDATE", changed, req.reason(), now)); // POL-01
        events.publishEvent(new CurationEvents.CardUpdated(card.getId(), curatorId, changed, now, req.reason()));
        return card;
    }

    private String buildChangedFields(UpdateCardRequest req) {
        StringBuilder sb = new StringBuilder();
        if (req.title() != null) sb.append("title,");
        if (req.category() != null) sb.append("category,");
        if (req.summaryLine1() != null || req.summaryLine2() != null || req.summaryLine3() != null) sb.append("summary,");
        if (req.scoreAxisDocs() != null || req.scoreAxisActivity() != null
                || req.scoreAxisPopularity() != null || req.scoreAxisMaintenance() != null) sb.append("score,");
        return sb.isEmpty() ? null : sb.substring(0, sb.length() - 1);
    }
}
