package com.aicommunity.rankingbatch.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingSnapshotRepository extends JpaRepository<RankingSnapshot, UUID> {
    Optional<RankingSnapshot> findBySnapshotYearAndWeekOfYear(int snapshotYear, int weekOfYear);
}
