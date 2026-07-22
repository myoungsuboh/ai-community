package com.aicommunity.curation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.curation.domain.AuditLog;
import com.aicommunity.curation.domain.AuditLogRepository;
import com.aicommunity.curation.domain.Card;
import com.aicommunity.curation.domain.CardRepository;
import com.aicommunity.curation.domain.Submission;
import com.aicommunity.curation.domain.SubmissionRepository;
import com.aicommunity.curation.domain.event.CurationEvents;
import com.aicommunity.curation.dto.SubmissionDtos.ReviewRequest;
import com.aicommunity.curation.dto.SubmissionDtos.ReviewResult;
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
class SubmissionServiceTest {

    @Mock SubmissionRepository submissionRepository;
    @Mock CardRepository cardRepository;
    @Mock AuditLogRepository auditLogRepository;
    @Mock ApplicationEventPublisher events;

    Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);
    SubmissionService service;

    final UUID member = UUID.randomUUID();
    final UUID curator = UUID.randomUUID();
    final String url = "https://github.com/microsoft/autogen";

    @BeforeEach
    void setUp() {
        service = new SubmissionService(submissionRepository, cardRepository, auditLogRepository, events, clock);
    }

    @Test
    @DisplayName("제보: 정상이면 저장 + SubmissionReceived 발행")
    void submit_ok() {
        when(submissionRepository.existsByUrlAndStatusIn(eq(url), anyCollection())).thenReturn(false);
        when(submissionRepository.countBySubmitterIdAndCreatedAtAfter(eq(member), any())).thenReturn(0L);

        Submission s = service.submit(member, url);

        assertThat(s.getUrl()).isEqualTo(url);
        verify(submissionRepository).save(any(Submission.class));
        verify(events).publishEvent(any(CurationEvents.SubmissionReceived.class));
    }

    @Test
    @DisplayName("제보: 잘못된 URL → INVALID_URL(422) (POL-05)")
    void submit_invalidUrl() {
        assertThatThrownBy(() -> service.submit(member, "not-a-url"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_URL);
        verify(submissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("제보: 중복 URL → DUPLICATE_SUBMISSION")
    void submit_duplicate() {
        when(submissionRepository.existsByUrlAndStatusIn(eq(url), anyCollection())).thenReturn(true);
        assertThatThrownBy(() -> service.submit(member, url))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.DUPLICATE_SUBMISSION);
    }

    @Test
    @DisplayName("제보: 하루 5건 초과 → SUBMISSION_LIMIT_EXCEEDED (POL-03)")
    void submit_rateLimited() {
        when(submissionRepository.existsByUrlAndStatusIn(eq(url), anyCollection())).thenReturn(false);
        when(submissionRepository.countBySubmitterIdAndCreatedAtAfter(eq(member), any())).thenReturn(5L);
        assertThatThrownBy(() -> service.submit(member, url))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.SUBMISSION_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("검수: PUBLISH → 카드 생성 + 감사로그 + CardPublished")
    void review_publish() {
        Submission s = Submission.create(url, member, OffsetDateTime.now(clock));
        when(submissionRepository.findById(s.getId())).thenReturn(Optional.of(s));
        when(cardRepository.existsBySlug(anyString())).thenReturn(false);

        ReviewRequest req = new ReviewRequest("PUBLISH", "AutoGen", "l1", "l2", "l3", "Agent",
                url, 22, 20, 19, 18, null);
        ReviewResult result = service.review(curator, s.getId(), req);

        assertThat(result.status()).isEqualTo("PUBLISHED");
        assertThat(result.cardSlug()).isNotBlank();
        verify(cardRepository).save(any(Card.class));
        verify(auditLogRepository).save(any(AuditLog.class)); // POL-01
        verify(events).publishEvent(any(CurationEvents.CardPublished.class));
    }

    @Test
    @DisplayName("검수: REJECT 사유 없음 → REJECTION_REASON_REQUIRED (POL-08)")
    void review_rejectRequiresReason() {
        Submission s = Submission.create(url, member, OffsetDateTime.now(clock));
        when(submissionRepository.findById(s.getId())).thenReturn(Optional.of(s));
        ReviewRequest req = new ReviewRequest("REJECT", null, null, null, null, null, null,
                null, null, null, null, "  ");
        assertThatThrownBy(() -> service.review(curator, s.getId(), req))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.REJECTION_REASON_REQUIRED);
    }

    @Test
    @DisplayName("검수: REJECT → 반려 처리 + CardRejected")
    void review_reject() {
        Submission s = Submission.create(url, member, OffsetDateTime.now(clock));
        when(submissionRepository.findById(s.getId())).thenReturn(Optional.of(s));
        ReviewRequest req = new ReviewRequest("REJECT", null, null, null, null, null, null,
                null, null, null, null, "품질 미달");
        ReviewResult result = service.review(curator, s.getId(), req);
        assertThat(result.status()).isEqualTo("REJECTED");
        verify(cardRepository, never()).save(any());
        verify(events).publishEvent(any(CurationEvents.CardRejected.class));
    }

    @Test
    @DisplayName("검수: 이미 검수된 제보 → CONFLICT")
    void review_nonPending() {
        Submission s = Submission.create(url, member, OffsetDateTime.now(clock));
        s.reject(curator, "x");
        when(submissionRepository.findById(s.getId())).thenReturn(Optional.of(s));
        ReviewRequest req = new ReviewRequest("PUBLISH", "T", null, null, null, "Agent", url, 1, 1, 1, 1, null);
        assertThatThrownBy(() -> service.review(curator, s.getId(), req))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.CONFLICT);
    }
}
