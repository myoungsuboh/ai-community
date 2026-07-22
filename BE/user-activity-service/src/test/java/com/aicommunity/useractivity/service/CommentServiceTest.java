package com.aicommunity.useractivity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.useractivity.domain.Comment;
import com.aicommunity.useractivity.domain.CommentReportRepository;
import com.aicommunity.useractivity.domain.CommentRepository;
import com.aicommunity.useractivity.domain.event.ActivityEvents;
import com.aicommunity.useractivity.dto.ActivityDtos.ReportResult;
import com.aicommunity.useractivity.support.AccountAgeChecker;
import com.aicommunity.useractivity.support.CardCounterUpdater;
import java.time.Clock;
import java.time.Instant;
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
class CommentServiceTest {

    @Mock CommentRepository commentRepository;
    @Mock CommentReportRepository reportRepository;
    @Mock CardCounterUpdater counters;
    @Mock AccountAgeChecker accountAge;
    @Mock ApplicationEventPublisher events;

    Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);
    CommentService service;
    final UUID user = UUID.randomUUID();
    final UUID card = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new CommentService(commentRepository, reportRepository, counters, accountAge, events, clock);
    }

    @Test
    @DisplayName("작성: 정상이면 저장 + 카운터(24h경과) + CommentCreated")
    void create_ok() {
        when(commentRepository.countByUserIdAndCreatedAtAfter(eq(user), any())).thenReturn(0L);
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(accountAge.eligibleForScoring(user)).thenReturn(true);

        Comment c = service.create(user, card, "좋은 카드네요");

        assertThat(c.getContent()).isEqualTo("좋은 카드네요");
        verify(counters).comments(eq(card), eq(1));
        verify(events).publishEvent(any(ActivityEvents.CommentCreated.class));
    }

    @Test
    @DisplayName("작성: 분당 3건 초과 → COMMENT_RATE_LIMITED (POL-02)")
    void create_rateLimited() {
        when(commentRepository.countByUserIdAndCreatedAtAfter(eq(user), any())).thenReturn(3L);
        assertThatThrownBy(() -> service.create(user, card, "또 댓글"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.COMMENT_RATE_LIMITED);
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("신고: 3건째면 자동 숨김 + CommentHidden (POL-18)")
    void report_autoHideAtThree() {
        Comment comment = Comment.create(card, UUID.randomUUID(), "댓글");
        comment.addReport(); // 1
        comment.addReport(); // 2 (아직 숨김 아님)
        UUID commentId = comment.getId();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(reportRepository.existsByCommentIdAndReporterId(commentId, user)).thenReturn(false);

        ReportResult result = service.report(user, commentId);

        assertThat(result.reportCount()).isEqualTo(3);
        assertThat(result.hidden()).isTrue();
        verify(events).publishEvent(any(ActivityEvents.CommentReported.class));
        verify(events).publishEvent(any(ActivityEvents.CommentHidden.class));
    }

    @Test
    @DisplayName("신고: 같은 사용자 중복 신고 → CONFLICT")
    void report_duplicate() {
        Comment comment = Comment.create(card, UUID.randomUUID(), "댓글");
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(reportRepository.existsByCommentIdAndReporterId(comment.getId(), user)).thenReturn(true);
        assertThatThrownBy(() -> service.report(user, comment.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.CONFLICT);
    }

    @Test
    @DisplayName("숨김/해제: 큐레이터가 상태 변경 + 이벤트")
    void setVisibility() {
        Comment comment = Comment.create(card, UUID.randomUUID(), "댓글");
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        UUID curator = UUID.randomUUID();

        service.setVisibility(curator, comment.getId(), true);
        assertThat(comment.isHidden()).isTrue();
        verify(events).publishEvent(any(ActivityEvents.CommentHidden.class));
    }
}
