package com.aicommunity.curation.service;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.curation.domain.AuditLog;
import com.aicommunity.curation.domain.AuditLogRepository;
import com.aicommunity.curation.domain.Card;
import com.aicommunity.curation.domain.CardRepository;
import com.aicommunity.curation.domain.Submission;
import com.aicommunity.curation.domain.SubmissionRepository;
import com.aicommunity.curation.domain.SubmissionStatus;
import com.aicommunity.curation.domain.event.CurationEvents;
import com.aicommunity.curation.dto.SubmissionDtos.ReviewRequest;
import com.aicommunity.curation.dto.SubmissionDtos.ReviewResult;
import com.aicommunity.curation.support.SlugGenerator;
import java.net.URI;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionService {

    private static final int DAILY_LIMIT = 5; // POL-03

    private final SubmissionRepository submissionRepository;
    private final CardRepository cardRepository;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public SubmissionService(SubmissionRepository submissionRepository, CardRepository cardRepository,
                             AuditLogRepository auditLogRepository, ApplicationEventPublisher events, Clock clock) {
        this.submissionRepository = submissionRepository;
        this.cardRepository = cardRepository;
        this.auditLogRepository = auditLogRepository;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public Submission submit(UUID submitterId, String url) {
        validateUrl(url); // POL-05 → 422

        // 중복: 이미 발행되었거나 검수 대기 중인 동일 URL 불가
        if (submissionRepository.existsByUrlAndStatusIn(url,
                List.of(SubmissionStatus.PENDING, SubmissionStatus.PUBLISHED))) {
            throw new BusinessException(ErrorCode.DUPLICATE_SUBMISSION);
        }

        // POL-03: 계정당 하루 5건
        OffsetDateTime startOfDay = OffsetDateTime.now(clock).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
        if (submissionRepository.countBySubmitterIdAndCreatedAtAfter(submitterId, startOfDay) >= DAILY_LIMIT) {
            throw new BusinessException(ErrorCode.SUBMISSION_LIMIT_EXCEEDED);
        }

        Submission submission = Submission.create(url, submitterId, OffsetDateTime.now(clock));
        submissionRepository.save(submission);
        events.publishEvent(new CurationEvents.SubmissionReceived(
                submission.getId(), submitterId, url, submission.getCreatedAt()));
        return submission;
    }

    @Transactional(readOnly = true)
    public Page<Submission> listByStatus(SubmissionStatus status, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return submissionRepository.findByStatus(status,
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.ASC, "createdAt")));
    }

    @Transactional
    public ReviewResult review(UUID curatorId, UUID submissionId, ReviewRequest req) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!submission.isPending()) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 검수된 제보입니다.");
        }

        OffsetDateTime now = OffsetDateTime.now(clock);

        if ("REJECT".equalsIgnoreCase(req.action())) {
            if (req.rejectionReason() == null || req.rejectionReason().isBlank()) {
                throw new BusinessException(ErrorCode.REJECTION_REASON_REQUIRED); // POL-08
            }
            submission.reject(curatorId, req.rejectionReason());
            submissionRepository.save(submission);
            events.publishEvent(new CurationEvents.CardRejected(
                    null, submission.getId(), curatorId, req.rejectionReason(), now));
            return new ReviewResult("REJECTED", submission.getId(), null, null);
        }

        // PUBLISH
        if (req.title() == null || req.title().isBlank() || req.category() == null || req.category().isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "발행에는 제목과 카테고리가 필요합니다.");
        }
        int docs = nz(req.scoreAxisDocs());
        int activity = nz(req.scoreAxisActivity());
        int popularity = nz(req.scoreAxisPopularity());
        int maintenance = nz(req.scoreAxisMaintenance());

        String slug = uniqueSlug(req.title());
        String repoUrl = (req.repoUrl() == null || req.repoUrl().isBlank()) ? submission.getUrl() : req.repoUrl();

        Card card = Card.publish(slug, req.title(), req.category(), repoUrl,
                req.summaryLine1(), req.summaryLine2(), req.summaryLine3(),
                docs, activity, popularity, maintenance, submission.getId(), now);
        cardRepository.save(card);

        submission.publish(curatorId, req.summaryLine1(), req.summaryLine2(), req.summaryLine3(), req.category());
        submissionRepository.save(submission);

        // POL-01: 발행 이력 기록
        auditLogRepository.save(AuditLog.of(card.getId(), curatorId, "PUBLISH", null, null, now));
        events.publishEvent(new CurationEvents.CardPublished(card.getId(), submission.getId(), curatorId, now));

        return new ReviewResult("PUBLISHED", submission.getId(), card.getId(), card.getSlug());
    }

    private void validateUrl(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equals("http") || scheme.equals("https")) || uri.getHost() == null) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_URL); // POL-05 → 422
        }
    }

    private String uniqueSlug(String title) {
        String slug = SlugGenerator.generate(title);
        while (cardRepository.existsBySlug(slug)) {
            slug = SlugGenerator.generate(title);
        }
        return slug;
    }

    private int nz(Integer v) {
        return v == null ? 0 : v;
    }
}
