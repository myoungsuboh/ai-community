package com.aicommunity.useractivity.web;

import com.aicommunity.common.security.UserPrincipal;
import com.aicommunity.useractivity.domain.ReactionType;
import com.aicommunity.useractivity.dto.ActivityDtos.ToggleReactionRequest;
import com.aicommunity.useractivity.dto.ActivityDtos.ToggleReactionResult;
import com.aicommunity.useractivity.service.ReactionService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cards/{cardId}/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    // 좋아요 및 북마크 토글 (회원)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ToggleReactionResult toggle(@AuthenticationPrincipal UserPrincipal user,
                                       @PathVariable UUID cardId,
                                       @Valid @RequestBody ToggleReactionRequest req) {
        return reactionService.toggle(user.id(), cardId, ReactionType.valueOf(req.type().name()));
    }
}
