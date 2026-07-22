package com.aicommunity.content.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.aicommunity.common.domain.CardStatus;
import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.content.domain.Card;
import com.aicommunity.content.domain.CardRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class CardQueryServiceTest {

    @Mock CardRepository cardRepository;
    CardQueryService service;

    @BeforeEach
    void setUp() {
        service = new CardQueryService(cardRepository);
    }

    @Test
    @DisplayName("피드: 최소점수 범위 밖(POL-13) → VALIDATION_FAILED")
    void feed_minScoreOutOfRange() {
        assertThatThrownBy(() -> service.feed(null, 150, "score", 0, 12))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.VALIDATION_FAILED);
    }

    @Test
    @DisplayName("피드: 허용되지 않은 정렬(POL-14) → VALIDATION_FAILED")
    void feed_invalidSort() {
        assertThatThrownBy(() -> service.feed(null, null, "hacky", 0, 12))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.VALIDATION_FAILED);
    }

    @Test
    @DisplayName("피드: 유효한 조건이면 정상 조회")
    void feed_valid() {
        Page<Card> empty = new PageImpl<>(java.util.List.of());
        when(cardRepository.feed(any(), any(), any())).thenReturn(empty);
        assertThatCode(() -> service.feed("Agent", 40, "recent", 0, 12)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상세: 없는 슬러그(POL-27) → CARD_NOT_FOUND")
    void detail_notFound() {
        when(cardRepository.findBySlugAndStatus(eq("nope"), eq(CardStatus.PUBLISHED)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.detail("nope"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.CARD_NOT_FOUND);
    }
}
