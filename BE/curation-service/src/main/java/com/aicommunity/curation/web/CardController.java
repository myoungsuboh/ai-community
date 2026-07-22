package com.aicommunity.curation.web;

import com.aicommunity.common.security.UserPrincipal;
import com.aicommunity.curation.dto.CardDtos.CardResponse;
import com.aicommunity.curation.dto.CardDtos.UpdateCardRequest;
import com.aicommunity.curation.service.CardService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // 발행 카드 수정 (큐레이터)
    @PatchMapping("/{cardId}")
    @PreAuthorize("hasAnyRole('CURATOR','ADMIN')")
    public CardResponse update(@AuthenticationPrincipal UserPrincipal user,
                               @PathVariable UUID cardId,
                               @Valid @RequestBody UpdateCardRequest req) {
        return CardResponse.from(cardService.update(user.id(), cardId, req));
    }
}
