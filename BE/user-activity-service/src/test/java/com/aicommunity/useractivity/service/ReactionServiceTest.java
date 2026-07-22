package com.aicommunity.useractivity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicommunity.useractivity.domain.Reaction;
import com.aicommunity.useractivity.domain.ReactionRepository;
import com.aicommunity.useractivity.domain.ReactionType;
import com.aicommunity.useractivity.domain.event.ActivityEvents;
import com.aicommunity.useractivity.dto.ActivityDtos.ToggleReactionResult;
import com.aicommunity.useractivity.support.AccountAgeChecker;
import com.aicommunity.useractivity.support.CardCounterUpdater;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock ReactionRepository reactionRepository;
    @Mock CardCounterUpdater counters;
    @Mock AccountAgeChecker accountAge;
    @Mock ApplicationEventPublisher events;

    Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);
    ReactionService service;
    final UUID user = UUID.randomUUID();
    final UUID card = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ReactionService(reactionRepository, counters, accountAge, events, clock);
    }

    @Test
    @DisplayName("토글: 없으면 추가 + 좋아요 카운터 +1 (24h 경과 계정)")
    void toggle_add() {
        when(reactionRepository.findByUserIdAndCardIdAndType(user, card, ReactionType.LIKE))
                .thenReturn(Optional.empty());
        when(accountAge.eligibleForScoring(user)).thenReturn(true);
        when(reactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ToggleReactionResult r = service.toggle(user, card, ReactionType.LIKE);

        assertThat(r.isAdded()).isTrue();
        verify(reactionRepository).save(any(Reaction.class));
        verify(counters).likes(eq(card), eq(1));
        verify(events).publishEvent(any(ActivityEvents.ReactionToggled.class));
    }

    @Test
    @DisplayName("토글: 있으면 해제 + 카운터 -1")
    void toggle_remove() {
        Reaction existing = Reaction.of(user, card, ReactionType.BOOKMARK, OffsetDateTime.now(clock));
        when(reactionRepository.findByUserIdAndCardIdAndType(user, card, ReactionType.BOOKMARK))
                .thenReturn(Optional.of(existing));
        when(accountAge.eligibleForScoring(user)).thenReturn(true);

        ToggleReactionResult r = service.toggle(user, card, ReactionType.BOOKMARK);

        assertThat(r.isAdded()).isFalse();
        verify(reactionRepository).delete(existing);
        verify(counters).bookmarks(eq(card), eq(-1));
    }

    @Test
    @DisplayName("POL-15: 24h 미만 계정은 반응 저장하되 커뮤니티 카운터 미반영")
    void toggle_ineligible_noCounter() {
        when(reactionRepository.findByUserIdAndCardIdAndType(user, card, ReactionType.LIKE))
                .thenReturn(Optional.empty());
        when(accountAge.eligibleForScoring(user)).thenReturn(false);
        when(reactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ToggleReactionResult r = service.toggle(user, card, ReactionType.LIKE);

        assertThat(r.isAdded()).isTrue();
        verify(reactionRepository).save(any(Reaction.class));
        verify(counters, never()).likes(any(), anyInt());
    }
}
