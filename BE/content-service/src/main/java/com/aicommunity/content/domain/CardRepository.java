package com.aicommunity.content.domain;

import com.aicommunity.common.domain.CardStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findBySlugAndStatus(String slug, CardStatus status);

    // 홈 피드: 발행된 카드 + 선택적 카테고리/최소점수 필터. 정렬은 Pageable 로 주입.
    @Query("""
            SELECT c FROM Card c
            WHERE c.status = com.aicommunity.common.domain.CardStatus.PUBLISHED
              AND (:category IS NULL OR c.category = :category)
              AND (:minScore IS NULL OR c.scoreTotal >= :minScore)
            """)
    Page<Card> feed(@Param("category") String category, @Param("minScore") Integer minScore, Pageable pageable);
}
