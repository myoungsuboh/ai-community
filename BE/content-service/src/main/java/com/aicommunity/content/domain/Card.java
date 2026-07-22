package com.aicommunity.content.domain;

import com.aicommunity.common.domain.CardStatus;
import com.aicommunity.common.entity.BaseTimeEntity;
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
 * Card (content 서비스 = 읽기 뷰). 홈 피드/카드 상세 조회 전용.
 * 쓰기(발행/수정)는 curation 서비스가 담당하며 같은 cards 테이블을 공유한다.
 */
@Entity
@Table(name = "cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card extends BaseTimeEntity {

    @Id
    private UUID id;

    private String slug;
    private String title;
    private String category;

    @Column(name = "repo_url")
    private String repoUrl;

    @Column(name = "summary_line1")
    private String summaryLine1;
    @Column(name = "summary_line2")
    private String summaryLine2;
    @Column(name = "summary_line3")
    private String summaryLine3;

    @Column(name = "score_total")
    private int scoreTotal;
    @Column(name = "score_axis_docs")
    private int scoreAxisDocs;
    @Column(name = "score_axis_activity")
    private int scoreAxisActivity;
    @Column(name = "score_axis_popularity")
    private int scoreAxisPopularity;
    @Column(name = "score_axis_maintenance")
    private int scoreAxisMaintenance;

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

    @Column(name = "meta_updated_at")
    private OffsetDateTime metaUpdatedAt;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;
}
