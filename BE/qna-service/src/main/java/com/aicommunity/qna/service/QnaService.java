package com.aicommunity.qna.service;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.common.support.HtmlSanitizer;
import com.aicommunity.qna.domain.Answer;
import com.aicommunity.qna.domain.AnswerRepository;
import com.aicommunity.qna.domain.Question;
import com.aicommunity.qna.domain.QuestionRepository;
import com.aicommunity.qna.domain.event.QnaEvents;
import com.aicommunity.qna.dto.QnaDtos.QuestionDetail;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QnaService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public QnaService(QuestionRepository questionRepository, AnswerRepository answerRepository,
                      ApplicationEventPublisher events, Clock clock) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public Question createQuestion(UUID authorId, String title, String content) {
        Question q = Question.create(HtmlSanitizer.stripAll(title), HtmlSanitizer.stripAll(content), authorId);
        questionRepository.save(q);
        events.publishEvent(new QnaEvents.QuestionCreated(q.getId(), authorId, q.getTitle(), OffsetDateTime.now(clock)));
        return q;
    }

    @Transactional(readOnly = true)
    public Page<Question> list(String keyword, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return questionRepository.search(kw, pageable);
    }

    @Transactional(readOnly = true)
    public QuestionDetail detail(UUID questionId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "질문을 찾을 수 없습니다."));
        return QuestionDetail.of(q, answerRepository.findByQuestionIdOrderByCreatedAtAsc(questionId));
    }

    @Transactional
    public Answer addAnswer(UUID authorId, UUID questionId, String content) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "질문을 찾을 수 없습니다."));
        Answer answer = answerRepository.save(Answer.create(questionId, HtmlSanitizer.stripAll(content), authorId));
        q.increaseAnswerCount();
        questionRepository.save(q);
        events.publishEvent(new QnaEvents.AnswerCreated(answer.getId(), questionId, authorId, OffsetDateTime.now(clock)));
        return answer;
    }
}
