package com.aicommunity.curation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Submission 애그리거트. 불변식(2_ddd.md): 계정당 하루 5건, 같은 URL 중복 불가, 3줄 요약 각 60자.
 * created_at 은 도메인 의미(제보 시각)로 직접 관리한다(BaseTimeEntity 미상속 — 스키마상 updated_at 없음).
 */
@Entity
@Table(name = "submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String url;

    @Column(name = "submitter_id", nullable = false)
    private UUID submitterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(name = "summary_line1")
    private String summaryLine1;

    @Column(name = "summary_line2")
    private String summaryLine2;

    @Column(name = "summary_line3")
    private String summaryLine3;

    @Column
    private String category;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static Submission create(String url, UUID submitterId, OffsetDateTime now) {
        Submission s = new Submission();
        s.id = UUID.randomUUID();
        s.url = url;
        s.submitterId = submitterId;
        s.status = SubmissionStatus.PENDING;
        s.createdAt = now;
        return s;
    }

    public void publish(UUID curatorId, String c1, String c2, String c3, String category) {
        this.status = SubmissionStatus.PUBLISHED;
        this.reviewedBy = curatorId;
        this.summaryLine1 = c1;
        this.summaryLine2 = c2;
        this.summaryLine3 = c3;
        this.category = category;
    }

    public void reject(UUID curatorId, String reason) {
        this.status = SubmissionStatus.REJECTED;
        this.reviewedBy = curatorId;
        this.rejectionReason = reason;
    }

    public boolean isPending() {
        return this.status == SubmissionStatus.PENDING;
    }
}
