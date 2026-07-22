package com.aicommunity.useractivity.domain;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 내 서재: 사용자가 북마크한 카드 조회 (반려 카드 포함 — POL-22 는 FE 표시). */
public interface LibraryCardRepository extends JpaRepository<Card, UUID> {

    @Query("""
            SELECT c FROM Card c
            WHERE c.id IN (
                SELECT r.cardId FROM Reaction r
                WHERE r.userId = :userId AND r.type = com.aicommunity.useractivity.domain.ReactionType.BOOKMARK
            )
            """)
    Page<Card> findBookmarkedByUser(@Param("userId") UUID userId, Pageable pageable);
}
