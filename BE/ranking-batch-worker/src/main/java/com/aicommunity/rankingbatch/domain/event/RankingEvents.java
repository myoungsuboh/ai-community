package com.aicommunity.rankingbatch.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/** 랭킹 도메인 이벤트 (2_ddd.md EVT-15). 인프로세스 발행. */
public final class RankingEvents {

    private RankingEvents() {
    }

    public record RankingSnapshotGenerated(UUID snapshotId, int weekOfYear, int year, OffsetDateTime generatedAt) {
    }
}
