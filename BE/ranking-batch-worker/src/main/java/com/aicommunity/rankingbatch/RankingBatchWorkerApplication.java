package com.aicommunity.rankingbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ranking-batch-worker — 진입점. base package "com.aicommunity" 를 스캔해 공용(common) 설정을 함께 로드한다.
 * 주간 랭킹 배치를 스케줄링한다(@EnableScheduling).
 */
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.aicommunity")
public class RankingBatchWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RankingBatchWorkerApplication.class, args);
    }
}
