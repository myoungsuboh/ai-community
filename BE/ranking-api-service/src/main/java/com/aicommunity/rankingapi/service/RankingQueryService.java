package com.aicommunity.rankingapi.service;

import com.aicommunity.common.domain.RankingEntry;
import com.aicommunity.rankingapi.domain.RankingSnapshot;
import com.aicommunity.rankingapi.domain.RankingSnapshotRepository;
import com.aicommunity.rankingapi.dto.RankingResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RankingQueryService {

    private static final TypeReference<List<RankingEntry>> ENTRY_LIST = new TypeReference<>() {
    };

    private final RankingSnapshotRepository repository;
    private final ObjectMapper objectMapper;

    public RankingQueryService(RankingSnapshotRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public RankingResponse weekly() {
        return repository.findTopByOrderBySnapshotYearDescWeekOfYearDesc()
                .map(this::toResponse)
                .orElseGet(RankingResponse::empty);
    }

    private RankingResponse toResponse(RankingSnapshot s) {
        List<RankingEntry> entries;
        try {
            entries = objectMapper.readValue(s.getEntries(), ENTRY_LIST);
        } catch (Exception e) {
            entries = List.of();
        }
        return new RankingResponse(s.getSnapshotYear(), s.getWeekOfYear(), s.getGeneratedAt(), entries);
    }
}
