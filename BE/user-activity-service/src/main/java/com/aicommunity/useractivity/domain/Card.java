package com.aicommunity.useractivity.domain;

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

/**
 * Card 읽기 뷰 (내 서재용). cards 테이블 공유 — curation 이 쓰고 여기서는 읽기만.
 */
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

    @Column(name = "star_count")
    private int starCount;
    @Column(name = "like_count")
    private int likeCount;
    @Column(name = "bookmark_count")
    private int bookmarkCount;
    @Column(name = "comment_count")
    private int commentCount;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "source_accessible")
    private boolean sourceAccessible;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;
}
