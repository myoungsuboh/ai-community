package com.aicommunity.qna.domain;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Query("""
            SELECT q FROM Question q
            WHERE (:keyword IS NULL OR LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Question> search(@Param("keyword") String keyword, Pageable pageable);
}
