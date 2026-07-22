package com.aicommunity.qna.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Q&A 도메인 이벤트 (2_ddd.md EVT-02, EVT-03). 인프로세스 발행. */
public final class QnaEvents {

    private QnaEvents() {
    }

    public record QuestionCreated(UUID questionId, UUID authorId, String title, OffsetDateTime createdAt) {
    }

    public record AnswerCreated(UUID answerId, UUID questionId, UUID authorId, OffsetDateTime createdAt) {
    }
}
