package com.aicommunity.rankingbatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RankingSnapshot 애그리거트. 주간 랭킹 스냅샷. (year, week_of_year) 유니크.
 * entries: 순위 배열 JSON (RankingEntry). generated_at: 생성 시각.
 */
@Entity
@Table(name = "ranking_snapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingSnapshot {

    @Id
    private UUID id;

    @Column(name = "snapshot_year", nullable = false)
    private int snapshotYear;

    @Column(name = "week_of_year", nullable = false)
    private int weekOfYear;

    @Column(nullable = false)
    private String entries;

    @Column(name = "generated_at", nullable = false)
    private OffsetDateTime generatedAt;

    public static RankingSnapshot create(int year, int week, String entriesJson, OffsetDateTime now) {
        RankingSnapshot s = new RankingSnapshot();
        s.id = UUID.randomUUID();
        s.snapshotYear = year;
        s.weekOfYear = week;
        s.entries = entriesJson;
        s.generatedAt = now;
        return s;
    }

    public void replaceEntries(String entriesJson, OffsetDateTime now) {
        this.entries = entriesJson;
        this.generatedAt = now;
    }
}
