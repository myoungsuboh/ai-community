package com.aicommunity.useractivity.service;

import com.aicommunity.useractivity.domain.Reaction;
import com.aicommunity.useractivity.domain.ReactionRepository;
import com.aicommunity.useractivity.domain.ReactionType;
import com.aicommunity.useractivity.domain.event.ActivityEvents;
import com.aicommunity.useractivity.dto.ActivityDtos.ToggleReactionResult;
import com.aicommunity.useractivity.support.AccountAgeChecker;
import com.aicommunity.useractivity.support.CardCounterUpdater;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final CardCounterUpdater counters;
    private final AccountAgeChecker accountAge;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public ReactionService(ReactionRepository reactionRepository, CardCounterUpdater counters,
                           AccountAgeChecker accountAge, ApplicationEventPublisher events, Clock clock) {
        this.reactionRepository = reactionRepository;
        this.counters = counters;
        this.accountAge = accountAge;
        this.events = events;
        this.clock = clock;
    }

    /** 좋아요/북마크 토글. 이미 있으면 해제(삭제), 없으면 추가. POL-04 는 uq 제약으로 강제. */
    @Transactional
    public ToggleReactionResult toggle(UUID userId, UUID cardId, ReactionType type) {
        Optional<Reaction> existing = reactionRepository.findByUserIdAndCardIdAndType(userId, cardId, type);
        boolean scoringEligible = accountAge.eligibleForScoring(userId); // POL-15

        boolean isAdded;
        UUID reactionId;
        if (existing.isPresent()) {
            reactionRepository.delete(existing.get());
            reactionId = existing.get().getId();
            isAdded = false;
            if (scoringEligible) {
                adjustCounter(type, cardId, -1);
            }
        } else {
            Reaction saved = reactionRepository.save(
                    Reaction.of(userId, cardId, type, OffsetDateTime.now(clock)));
            reactionId = saved.getId();
            isAdded = true;
            if (scoringEligible) {
                adjustCounter(type, cardId, +1);
            }
        }

        events.publishEvent(new ActivityEvents.ReactionToggled(
                reactionId, userId, cardId, type.name(), isAdded, OffsetDateTime.now(clock)));
        return new ToggleReactionResult(type.name(), isAdded);
    }

    private void adjustCounter(ReactionType type, UUID cardId, int delta) {
        if (type == ReactionType.LIKE) {
            counters.likes(cardId, delta);
        } else {
            counters.bookmarks(cardId, delta);
        }
    }
}
