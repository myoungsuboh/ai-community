package com.aicommunity.rankingbatch.service;

import com.aicommunity.common.domain.RankingEntry;
import com.aicommunity.rankingbatch.domain.Card;
import com.aicommunity.rankingbatch.domain.CardRankingRepository;
import com.aicommunity.rankingbatch.domain.RankingSnapshot;
import com.aicommunity.rankingbatch.domain.RankingSnapshotRepository;
import com.aicommunity.rankingbatch.domain.event.RankingEvents;
import com.aicommunity.rankingbatch.service.RankingCalculator.CardInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 주간 랭킹 배치. 매주 월요일 0시(UTC) 실행 + 수동 트리거.
 * 산정 대상: 발행 7일 경과 카드(POL-23). 산정식/정렬: RankingCalculator.
 */
@Service
public class RankingBatchService {

    private static final Logger log = LoggerFactory.getLogger(RankingBatchService.class);

    private final CardRankingRepository cardRepository;
    private final RankingSnapshotRepository snapshotRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public RankingBatchService(CardRankingRepository cardRepository, RankingSnapshotRepository snapshotRepository,
                               ObjectMapper objectMapper, ApplicationEventPublisher events, Clock clock) {
        this.cardRepository = cardRepository;
        this.snapshotRepository = snapshotRepository;
        this.objectMapper = objectMapper;
        this.events = events;
        this.clock = clock;
    }

    // 매주 월요일 0시 0분 (UTC) — story_06_1
    @Scheduled(cron = "0 0 0 * * MON", zone = "UTC")
    public void scheduledRun() {
        log.info("Weekly ranking batch triggered by schedule");
        run();
    }

    @Transactional
    public RankingSnapshot run() {
        OffsetDateTime now = OffsetDateTime.now(clock);
        OffsetDateTime cutoff = now.minus(Duration.ofDays(7)); // POL-23

        List<CardInput> inputs = cardRepository.findEligible(cutoff).stream()
                .map(c -> new CardInput(c.getId(), c.getSlug(), c.getTitle(), c.getCategory(),
                        c.getScoreTotal(), c.getLikeCount(), c.getBookmarkCount(), c.getCommentCount()))
                .toList();
        List<RankingEntry> entries = RankingCalculator.rank(inputs);
        String json = toJson(entries);

        int year = now.get(WeekFields.ISO.weekBasedYear());
        int week = now.get(WeekFields.ISO.weekOfWeekBasedYear());

        RankingSnapshot snapshot = snapshotRepository.findBySnapshotYearAndWeekOfYear(year, week)
                .map(existing -> {
                    existing.replaceEntries(json, now);
                    return existing;
                })
                .orElseGet(() -> RankingSnapshot.create(year, week, json, now));
        snapshotRepository.save(snapshot);

        events.publishEvent(new RankingEvents.RankingSnapshotGenerated(
                snapshot.getId(), week, year, now));
        log.info("Ranking snapshot {}-W{} generated with {} entries", year, week, entries.size());
        return snapshot;
    }

    private String toJson(List<RankingEntry> entries) {
        try {
            return objectMapper.writeValueAsString(entries);
        } catch (Exception e) {
            throw new IllegalStateException("랭킹 직렬화 실패", e);
        }
    }
}
