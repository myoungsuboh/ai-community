package com.aicommunity.qna.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.qna.domain.Answer;
import com.aicommunity.qna.domain.AnswerRepository;
import com.aicommunity.qna.domain.Question;
import com.aicommunity.qna.domain.QuestionRepository;
import com.aicommunity.qna.domain.event.QnaEvents;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class QnaServiceTest {

    @Mock QuestionRepository questionRepository;
    @Mock AnswerRepository answerRepository;
    @Mock ApplicationEventPublisher events;

    Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);
    QnaService service;
    final UUID user = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new QnaService(questionRepository, answerRepository, events, clock);
    }

    @Test
    @DisplayName("질문 등록: 저장 + QuestionCreated")
    void createQuestion() {
        when(questionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Question q = service.createQuestion(user, "제목", "질문 내용입니다");
        assertThat(q.getAuthorId()).isEqualTo(user);
        verify(questionRepository).save(any(Question.class));
        verify(events).publishEvent(any(QnaEvents.QuestionCreated.class));
    }

    @Test
    @DisplayName("답변 등록: 질문 존재 → 답변 저장 + 답변수 증가 + AnswerCreated")
    void addAnswer() {
        Question q = Question.create("t", "c", UUID.randomUUID());
        when(questionRepository.findById(q.getId())).thenReturn(Optional.of(q));
        when(answerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Answer a = service.addAnswer(user, q.getId(), "답변 내용");

        assertThat(a.getContent()).isEqualTo("답변 내용");
        assertThat(q.getAnswerCount()).isEqualTo(1);
        verify(questionRepository).save(q);
        verify(events).publishEvent(any(QnaEvents.AnswerCreated.class));
    }

    @Test
    @DisplayName("답변 등록: 없는 질문 → NOT_FOUND")
    void addAnswer_questionNotFound() {
        when(questionRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.addAnswer(user, UUID.randomUUID(), "답변"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("상세: 없는 질문 → NOT_FOUND")
    void detail_notFound() {
        when(questionRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.detail(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_FOUND);
    }
}
