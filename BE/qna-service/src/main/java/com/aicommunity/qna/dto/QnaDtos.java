package com.aicommunity.qna.dto;

import com.aicommunity.qna.domain.Answer;
import com.aicommunity.qna.domain.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class QnaDtos {

    private QnaDtos() {
    }

    // POL-11: 질문 내용 비어있을 수 없음
    public record CreateQuestionRequest(
            @NotBlank(message = "제목을 입력해 주세요.") @Size(max = 200) String title,
            @NotBlank(message = "질문 내용을 입력해 주세요.") String content) {
    }

    // POL-11: 답변 내용 비어있을 수 없음
    public record CreateAnswerRequest(
            @NotBlank(message = "답변 내용을 입력해 주세요.") String content) {
    }

    public record AnswerResponse(UUID answerId, UUID questionId, String content, UUID authorId,
                                 OffsetDateTime createdAt) {
        public static AnswerResponse from(Answer a) {
            return new AnswerResponse(a.getId(), a.getQuestionId(), a.getContent(), a.getAuthorId(), a.getCreatedAt());
        }
    }

    public record QuestionSummary(UUID questionId, String title, UUID authorId, int answerCount,
                                  OffsetDateTime createdAt) {
        public static QuestionSummary from(Question q) {
            return new QuestionSummary(q.getId(), q.getTitle(), q.getAuthorId(), q.getAnswerCount(), q.getCreatedAt());
        }
    }

    public record QuestionDetail(UUID questionId, String title, String content, UUID authorId,
                                 int answerCount, OffsetDateTime createdAt, List<AnswerResponse> answers) {
        public static QuestionDetail of(Question q, List<Answer> answers) {
            return new QuestionDetail(q.getId(), q.getTitle(), q.getContent(), q.getAuthorId(),
                    q.getAnswerCount(), q.getCreatedAt(), answers.stream().map(AnswerResponse::from).toList());
        }
    }
}
