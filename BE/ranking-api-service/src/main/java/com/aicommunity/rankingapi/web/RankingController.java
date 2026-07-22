package com.aicommunity.rankingapi.web;

import com.aicommunity.rankingapi.dto.RankingResponse;
import com.aicommunity.rankingapi.service.RankingQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rankings")
public class RankingController {

    private final RankingQueryService rankingQueryService;

    public RankingController(RankingQueryService rankingQueryService) {
        this.rankingQueryService = rankingQueryService;
    }

    // 주간 랭킹 조회 (공개)
    @GetMapping("/weekly")
    public RankingResponse weekly() {
        return rankingQueryService.weekly();
    }
}
