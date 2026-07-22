package com.aicommunity.curation.dto;

import com.aicommunity.curation.domain.Submission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class SubmissionDtos {

    private SubmissionDtos() {
    }

    public record CreateSubmissionRequest(
            @NotBlank(message = "제보할 URL을 입력해 주세요.") String url) {
    }

    public record SubmissionResponse(UUID submissionId, String url, String status, OffsetDateTime createdAt) {
        public static SubmissionResponse from(Submission s) {
            return new SubmissionResponse(s.getId(), s.getUrl(), s.getStatus().name(), s.getCreatedAt());
        }
    }

    /** 큐레이터 검수함 목록 항목. */
    public record SubmissionSummary(UUID submissionId, String url, UUID submitterId, String status,
                                    OffsetDateTime createdAt) {
        public static SubmissionSummary from(Submission s) {
            return new SubmissionSummary(s.getId(), s.getUrl(), s.getSubmitterId(),
                    s.getStatus().name(), s.getCreatedAt());
        }
    }

    /** 검수: action=PUBLISH 면 요약/카테고리/4축점수 필수, REJECT 면 rejectionReason 필수(POL-08). */
    public record ReviewRequest(
            @NotBlank String action, // PUBLISH | REJECT
            String title,
            @Size(max = 60, message = "요약 각 줄은 최대 60자입니다.") String summaryLine1,
            @Size(max = 60) String summaryLine2,
            @Size(max = 60) String summaryLine3,
            String category,
            String repoUrl,
            Integer scoreAxisDocs,
            Integer scoreAxisActivity,
            Integer scoreAxisPopularity,
            Integer scoreAxisMaintenance,
            String rejectionReason) {
    }

    public record ReviewResult(String status, UUID submissionId, UUID cardId, String cardSlug) {
    }
}
