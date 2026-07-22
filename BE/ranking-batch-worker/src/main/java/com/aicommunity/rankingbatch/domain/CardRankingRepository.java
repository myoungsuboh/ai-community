package com.aicommunity.rankingbatch.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardRankingRepository extends JpaRepository<Card, UUID> {

    // POL-23: 발행 7일이 지난(published_at <= cutoff) 발행 카드만 랭킹 대상
    @Query("""
            SELECT c FROM Card c
            WHERE c.status = com.aicommunity.common.domain.CardStatus.PUBLISHED
              AND c.publishedAt IS NOT NULL
              AND c.publishedAt <= :cutoff
            """)
    List<Card> findEligible(@Param("cutoff") OffsetDateTime cutoff);
}
