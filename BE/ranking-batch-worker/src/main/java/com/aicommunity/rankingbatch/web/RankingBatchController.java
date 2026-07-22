package com.aicommunity.rankingbatch.web;

import com.aicommunity.rankingbatch.domain.RankingSnapshot;
import com.aicommunity.rankingbatch.service.RankingBatchService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 배치 수동 트리거 (운영/검증용). 스케줄과 별개로 관리자가 즉시 실행.
 * 아키텍처상 워커는 무포트지만 로컬 운영·검증을 위해 최소 트리거 엔드포인트를 제공한다.
 */
@RestController
@RequestMapping("/internal/rankings")
public class RankingBatchController {

    private final RankingBatchService batchService;

    public RankingBatchController(RankingBatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')")
    public RunResult run() {
        RankingSnapshot s = batchService.run();
        return new RunResult(s.getSnapshotYear(), s.getWeekOfYear(), s.getGeneratedAt().toString());
    }

    public record RunResult(int year, int week, String generatedAt) {
    }
}
