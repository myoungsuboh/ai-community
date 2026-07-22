package com.aicommunity.curation.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/** 큐레이션/콘텐츠 도메인 이벤트 (2_ddd.md EVT-01,06,07,08,09). 인프로세스 발행. */
public final class CurationEvents {

    private CurationEvents() {
    }

    public record PostCreated(UUID postId, UUID authorId, String title, OffsetDateTime createdAt) {
    }

    public record SubmissionReceived(UUID submissionId, UUID submitterId, String url, OffsetDateTime createdAt) {
    }

    public record CardPublished(UUID cardId, UUID submissionId, UUID curatorId, OffsetDateTime publishedAt) {
    }

    public record CardRejected(UUID cardId, UUID submissionId, UUID curatorId, String rejectionReason,
                               OffsetDateTime rejectedAt) {
    }

    public record CardUpdated(UUID cardId, UUID modifierId, String updatedFields, OffsetDateTime updatedAt,
                              String reason) {
    }
}
