package com.aicommunity.useractivity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 신고 이력. (사용자 결정: 사유 없이 신고만) 계정당 댓글당 1회(uq 제약).
 */
@Entity
@Table(name = "comment_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReport {

    @Id
    private UUID id;

    @Column(name = "comment_id", nullable = false)
    private UUID commentId;

    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static CommentReport of(UUID commentId, UUID reporterId, OffsetDateTime now) {
        CommentReport r = new CommentReport();
        r.id = UUID.randomUUID();
        r.commentId = commentId;
        r.reporterId = reporterId;
        r.createdAt = now;
        return r;
    }
}
