package com.aicommunity.rankingapi.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingSnapshotRepository extends JpaRepository<RankingSnapshot, UUID> {
    // 가장 최근 주(연도·주차 내림차순)의 스냅샷
    Optional<RankingSnapshot> findTopByOrderBySnapshotYearDescWeekOfYearDesc();
}
