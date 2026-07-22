package com.aicommunity.curation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.curation.domain.AuditLog;
import com.aicommunity.curation.domain.AuditLogRepository;
import com.aicommunity.curation.domain.Card;
import com.aicommunity.curation.domain.CardRepository;
import com.aicommunity.curation.domain.event.CurationEvents;
import com.aicommunity.curation.dto.CardDtos.UpdateCardRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock CardRepository cardRepository;
    @Mock AuditLogRepository auditLogRepository;
    @Mock ApplicationEventPublisher events;

    Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);
    CardService service;
    final UUID curator = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new CardService(cardRepository, auditLogRepository, events, clock);
    }

    private Card publishedCard() {
        return Card.publish("slug-1", "T", "Agent", "https://x.y", "a", "b", "c",
                20, 20, 20, 20, UUID.randomUUID(), OffsetDateTime.now(clock));
    }

    @Test
    @DisplayName("수정: 실전점수 변경 + 사유 없음 → SCORE_REASON_REQUIRED (POL-10)")
    void update_scoreChange_noReason() {
        Card card = publishedCard();
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        UpdateCardRequest req = new UpdateCardRequest(null, null, null, null, null, 25, null, null, null, null);
        assertThatThrownBy(() -> service.update(curator, UUID.randomUUID(), req))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.SCORE_REASON_REQUIRED);
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("수정: 점수 변경 + 사유 → 저장 + 감사로그(POL-01) + CardUpdated")
    void update_scoreChange_withReason() {
        Card card = publishedCard();
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        UpdateCardRequest req = new UpdateCardRequest(null, null, null, null, null, 25, null, null, null, "근거");
        service.update(curator, UUID.randomUUID(), req);
        verify(cardRepository).save(card);
        verify(auditLogRepository).save(any(AuditLog.class));
        verify(events).publishEvent(any(CurationEvents.CardUpdated.class));
    }

    @Test
    @DisplayName("수정: 점수 변경 없음 → 사유 없이도 저장")
    void update_noScoreChange() {
        Card card = publishedCard();
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        UpdateCardRequest req = new UpdateCardRequest("새 제목", null, null, null, null, null, null, null, null, null);
        service.update(curator, UUID.randomUUID(), req);
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("수정: 없는 카드 → CARD_NOT_FOUND")
    void update_notFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());
        UpdateCardRequest req = new UpdateCardRequest("T", null, null, null, null, null, null, null, null, null);
        assertThatThrownBy(() -> service.update(curator, UUID.randomUUID(), req))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.CARD_NOT_FOUND);
    }
}
