package com.aicommunity.useractivity.service;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.common.support.HtmlSanitizer;
import com.aicommunity.useractivity.domain.Comment;
import com.aicommunity.useractivity.domain.CommentReport;
import com.aicommunity.useractivity.domain.CommentReportRepository;
import com.aicommunity.useractivity.domain.CommentRepository;
import com.aicommunity.useractivity.domain.event.ActivityEvents;
import com.aicommunity.useractivity.dto.ActivityDtos.ReportResult;
import com.aicommunity.useractivity.support.AccountAgeChecker;
import com.aicommunity.useractivity.support.CardCounterUpdater;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private static final int PER_MINUTE_LIMIT = 3; // POL-02

    private final CommentRepository commentRepository;
    private final CommentReportRepository reportRepository;
    private final CardCounterUpdater counters;
    private final AccountAgeChecker accountAge;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public CommentService(CommentRepository commentRepository, CommentReportRepository reportRepository,
                          CardCounterUpdater counters, AccountAgeChecker accountAge,
                          ApplicationEventPublisher events, Clock clock) {
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
        this.counters = counters;
        this.accountAge = accountAge;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public Comment create(UUID userId, UUID cardId, String rawContent) {
        // POL-02: 분당 3건 제한
        OffsetDateTime windowStart = OffsetDateTime.now(clock).minus(Duration.ofMinutes(1));
        if (commentRepository.countByUserIdAndCreatedAtAfter(userId, windowStart) >= PER_MINUTE_LIMIT) {
            throw new BusinessException(ErrorCode.COMMENT_RATE_LIMITED);
        }
        String content = HtmlSanitizer.stripAll(rawContent); // XSS 방지
        Comment comment = commentRepository.save(Comment.create(cardId, userId, content));
        if (accountAge.eligibleForScoring(userId)) { // POL-15
            counters.comments(cardId, +1);
        }
        events.publishEvent(new ActivityEvents.CommentCreated(
                comment.getId(), cardId, userId, content, comment.getCreatedAt()));
        return comment;
    }

    @Transactional(readOnly = true)
    public Page<Comment> list(UUID cardId, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return commentRepository.findByCardIdAndHiddenFalseAndDeletedAtIsNull(cardId,
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional
    public ReportResult report(UUID reporterId, UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "댓글을 찾을 수 없습니다."));
        if (reportRepository.existsByCommentIdAndReporterId(commentId, reporterId)) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 신고한 댓글입니다.");
        }
        OffsetDateTime now = OffsetDateTime.now(clock);
        reportRepository.save(CommentReport.of(commentId, reporterId, now));

        boolean autoHidden = comment.addReport(); // POL-18: 3건 → 자동 숨김
        commentRepository.save(comment);

        events.publishEvent(new ActivityEvents.CommentReported(commentId, comment.getCardId(), reporterId, now));
        if (autoHidden) {
            events.publishEvent(new ActivityEvents.CommentHidden(
                    commentId, comment.getCardId(), "SYSTEM", now, "자동 숨김(신고 누적 3건)"));
        }
        return new ReportResult(commentId, comment.getReportCount(), comment.isHidden());
    }

    @Transactional
    public Comment setVisibility(UUID curatorId, UUID commentId, boolean hidden) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "댓글을 찾을 수 없습니다."));
        comment.setHidden(hidden);
        commentRepository.save(comment);
        OffsetDateTime now = OffsetDateTime.now(clock);
        if (hidden) {
            events.publishEvent(new ActivityEvents.CommentHidden(
                    commentId, comment.getCardId(), curatorId.toString(), now, "큐레이터 숨김"));
        } else {
            events.publishEvent(new ActivityEvents.CommentUnhidden(commentId, comment.getCardId(), curatorId, now));
        }
        return comment;
    }
}
