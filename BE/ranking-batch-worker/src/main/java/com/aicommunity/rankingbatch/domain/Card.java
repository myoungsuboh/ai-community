package com.aicommunity.rankingbatch.domain;

import com.aicommunity.common.domain.CardStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Card 읽기 뷰 (랭킹 산정 입력). cards 테이블 공유. */
@Entity
@Table(name = "cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {

    @Id
    private UUID id;

    private String slug;
    private String title;
    private String category;

    @Column(name = "score_total")
    private int scoreTotal;
    @Column(name = "like_count")
    private int likeCount;
    @Column(name = "bookmark_count")
    private int bookmarkCount;
    @Column(name = "comment_count")
    private int commentCount;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;
}
