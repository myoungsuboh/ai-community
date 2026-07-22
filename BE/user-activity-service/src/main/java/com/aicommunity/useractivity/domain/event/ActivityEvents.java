package com.aicommunity.useractivity.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/** 사용자 활동 도메인 이벤트 (2_ddd.md EVT-10~14). 인프로세스 발행. */
public final class ActivityEvents {

    private ActivityEvents() {
    }

    public record ReactionToggled(UUID reactionId, UUID userId, UUID cardId, String type, boolean isAdded,
                                  OffsetDateTime toggledAt) {
    }

    public record CommentCreated(UUID commentId, UUID cardId, UUID userId, String content,
                                 OffsetDateTime createdAt) {
    }

    public record CommentReported(UUID commentId, UUID cardId, UUID reporterId, OffsetDateTime reportedAt) {
    }

    public record CommentHidden(UUID commentId, UUID cardId, String hiddenBy, OffsetDateTime hiddenAt, String reason) {
    }

    public record CommentUnhidden(UUID commentId, UUID cardId, UUID unhiddenBy, OffsetDateTime unhiddenAt) {
    }
}
