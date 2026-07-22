package com.aicommunity.qna.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByQuestionIdOrderByCreatedAtAsc(UUID questionId);
}
