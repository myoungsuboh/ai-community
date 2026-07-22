package com.aicommunity.rankingapi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** RankingSnapshot 읽기 뷰 (주간 랭킹 조회). ranking-batch-worker 가 쓰고 여기서 읽는다. */
@Entity
@Table(name = "ranking_snapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingSnapshot {

    @Id
    private UUID id;

    @Column(name = "snapshot_year")
    private int snapshotYear;

    @Column(name = "week_of_year")
    private int weekOfYear;

    private String entries;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;
}
