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
 * Question 애그리거트. 불변식(2_ddd.md): 질문 내용은 비어있을 수 없음(POL-11, DTO 검증).
 */
@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "answer_count", nullable = false)
    private int answerCount;

    public static Question create(String title, String content, UUID authorId) {
        Question q = new Question();
        q.id = UUID.randomUUID();
        q.title = title;
        q.content = content;
        q.authorId = authorId;
        q.answerCount = 0;
        return q;
    }

    public void increaseAnswerCount() {
        this.answerCount++;
    }
}
