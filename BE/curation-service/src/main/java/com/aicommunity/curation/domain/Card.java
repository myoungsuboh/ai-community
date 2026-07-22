package com.aicommunity.curation.domain;

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
 * Card 애그리거트 (큐레이션 = 쓰기 소유; content 서비스는 읽기).
 * 불변식(2_ddd.md): 4축 합계=총점(POL-09), 원본 접근 불가 시 뱃지/점수갱신중단, 실전점수 색상 규칙(FE).
 */
@Entity
@Table(name = "cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    @Column(name = "summary_line1")
    private String summaryLine1;
    @Column(name = "summary_line2")
    private String summaryLine2;
    @Column(name = "summary_line3")
    private String summaryLine3;

    @Column(name = "score_total", nullable = false)
    private int scoreTotal;
    @Column(name = "score_axis_docs", nullable = false)
    private int scoreAxisDocs;
    @Column(name = "score_axis_activity", nullable = false)
    private int scoreAxisActivity;
    @Column(name = "score_axis_popularity", nullable = false)
    private int scoreAxisPopularity;
    @Column(name = "score_axis_maintenance", nullable = false)
    private int scoreAxisMaintenance;

    @Column(name = "star_count", nullable = false)
    private int starCount;
    @Column(name = "like_count", nullable = false)
    private int likeCount;
    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount;
    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    @Column(name = "source_accessible", nullable = false)
    private boolean sourceAccessible;

    @Column(name = "meta_updated_at")
    private OffsetDateTime metaUpdatedAt;

    @Column(name = "submission_id")
    private UUID submissionId;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    /** 제보 검수 발행으로 카드 생성. 4축 합계=총점 강제(POL-09) — 서비스에서 사전 검증. */
    public static Card publish(String slug, String title, String category, String repoUrl,
                               String s1, String s2, String s3,
                               int docs, int activity, int popularity, int maintenance,
                               UUID submissionId, OffsetDateTime now) {
        Card c = new Card();
        c.id = UUID.randomUUID();
        c.slug = slug;
        c.title = title;
        c.category = category;
        c.repoUrl = repoUrl;
        c.summaryLine1 = s1;
        c.summaryLine2 = s2;
        c.summaryLine3 = s3;
        c.scoreAxisDocs = docs;
        c.scoreAxisActivity = activity;
        c.scoreAxisPopularity = popularity;
        c.scoreAxisMaintenance = maintenance;
        c.scoreTotal = docs + activity + popularity + maintenance;
        c.status = CardStatus.PUBLISHED;
        c.sourceAccessible = true;
        c.submissionId = submissionId;
        c.publishedAt = now;
        c.metaUpdatedAt = now;
        return c;
    }

    /** 발행 카드 정보 수정. 점수 축이 바뀌면 총점 재계산. */
    public void updateInfo(String title, String category, String s1, String s2, String s3,
                           Integer docs, Integer activity, Integer popularity, Integer maintenance) {
        if (title != null) this.title = title;
        if (category != null) this.category = category;
        if (s1 != null) this.summaryLine1 = s1;
        if (s2 != null) this.summaryLine2 = s2;
        if (s3 != null) this.summaryLine3 = s3;
        if (docs != null) this.scoreAxisDocs = docs;
        if (activity != null) this.scoreAxisActivity = activity;
        if (popularity != null) this.scoreAxisPopularity = popularity;
        if (maintenance != null) this.scoreAxisMaintenance = maintenance;
        this.scoreTotal = this.scoreAxisDocs + this.scoreAxisActivity
                + this.scoreAxisPopularity + this.scoreAxisMaintenance;
    }

    public boolean scoreChanged(Integer docs, Integer activity, Integer popularity, Integer maintenance) {
        return (docs != null && docs != scoreAxisDocs)
                || (activity != null && activity != scoreAxisActivity)
                || (popularity != null && popularity != scoreAxisPopularity)
                || (maintenance != null && maintenance != scoreAxisMaintenance);
    }
}
