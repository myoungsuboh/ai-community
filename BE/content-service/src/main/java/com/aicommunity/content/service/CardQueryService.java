package com.aicommunity.content.service;

import com.aicommunity.common.domain.CardStatus;
import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.content.domain.Card;
import com.aicommunity.content.domain.CardRepository;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카드 조회(홈 피드/상세). 필터·정렬 화이트리스트(POL-13/14), 미존재 슬러그 404(POL-27).
 */
@Service
public class CardQueryService {

    private static final Set<String> SORTS = Set.of("score", "recent", "stars");

    private final CardRepository cardRepository;

    public CardQueryService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional(readOnly = true)
    public Page<Card> feed(String category, Integer minScore, String sort, int page, int size) {
        // POL-13: 최소점수 0~100
        if (minScore != null && (minScore < 0 || minScore > 100)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "최소점수는 0~100 사이여야 합니다.");
        }
        // POL-14: 정렬 화이트리스트
        String sortKey = (sort == null || sort.isBlank()) ? "score" : sort.toLowerCase();
        if (!SORTS.contains(sortKey)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "정렬 기준이 올바르지 않습니다.");
        }
        String cat = (category == null || category.isBlank()) ? null : category;
        int safeSize = Math.min(Math.max(size, 1), 100);

        return cardRepository.feed(cat, minScore, PageRequest.of(Math.max(page, 0), safeSize, sortOf(sortKey)));
    }

    @Transactional(readOnly = true)
    public Card detail(String slug) {
        return cardRepository.findBySlugAndStatus(slug, CardStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_NOT_FOUND)); // POL-27 → 404
    }

    private Sort sortOf(String key) {
        return switch (key) {
            case "recent" -> Sort.by(Sort.Direction.DESC, "publishedAt");
            case "stars" -> Sort.by(Sort.Direction.DESC, "starCount");
            // 점수순: 총점 내림차순, 동점 시 북마크 많은 카드 우선(POL-19 정신)
            default -> Sort.by(Sort.Order.desc("scoreTotal"), Sort.Order.desc("bookmarkCount"));
        };
    }
}
