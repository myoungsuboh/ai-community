package com.aicommunity.content.web;

import com.aicommunity.common.web.PageResponse;
import com.aicommunity.content.domain.Card;
import com.aicommunity.content.dto.CardDtos.CardDetail;
import com.aicommunity.content.dto.CardDtos.CardSummary;
import com.aicommunity.content.service.CardQueryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardQueryService cardQueryService;

    public CardController(CardQueryService cardQueryService) {
        this.cardQueryService = cardQueryService;
    }

    // 홈 피드 카드 목록 조회 및 필터링 (공개)
    @GetMapping
    public PageResponse<CardSummary> feed(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(defaultValue = "score") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<Card> result = cardQueryService.feed(category, minScore, sort, page, size);
        return PageResponse.from(result, result.map(CardSummary::from).getContent());
    }

    // 카드 상세 조회 (공개)
    @GetMapping("/{cardSlug}")
    public CardDetail detail(@PathVariable String cardSlug) {
        return CardDetail.from(cardQueryService.detail(cardSlug));
    }
}
