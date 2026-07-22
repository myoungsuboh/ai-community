package com.aicommunity.rankingapi.dto;

import com.aicommunity.common.domain.RankingEntry;
import java.time.OffsetDateTime;
import java.util.List;

/** 주간 랭킹 응답. 스냅샷이 없으면 entries 는 빈 배열. */
public record RankingResponse(
        Integer year,
        Integer week,
        OffsetDateTime generatedAt,
        List<RankingEntry> entries
) {
    public static RankingResponse empty() {
        return new RankingResponse(null, null, null, List.of());
    }
}
