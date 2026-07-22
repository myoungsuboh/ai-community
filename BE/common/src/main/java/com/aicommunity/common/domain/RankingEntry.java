package com.aicommunity.common.domain;

import java.util.UUID;

/**
 * 주간 랭킹 항목. ranking_snapshots.entries 컬럼에 JSON 배열로 저장된다.
 * ranking-batch-worker(쓰기)와 ranking-api-service(읽기)가 공유하는 직렬화 스키마.
 */
public record RankingEntry(
        int rank,
        UUID cardId,
        String slug,
        String title,
        String category,
        double score,
        int likeCount,
        int bookmarkCount,
        int commentCount,
        int scoreTotal
) {
}
