package com.aicommunity.qna.domain;

import com.aicommunity.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Answer (소속 Aggregate: Question). 질문 삭제 시 함께 삭제(POL-28: FK ON DELETE CASCADE, V1).
 */
@Entity
@Table(name = "answers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(nullable = false)
    private String content;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    public static Answer create(UUID questionId, String content, UUID authorId) {
        Answer a = new Answer();
        a.id = UUID.randomUUID();
        a.questionId = questionId;
        a.content = content;
        a.authorId = authorId;
        return a;
    }
}
