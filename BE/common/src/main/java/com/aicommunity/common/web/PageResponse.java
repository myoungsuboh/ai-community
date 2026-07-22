package com.aicommunity.common.web;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * 표준 페이지네이션 응답 (skills/backEnd/pagination-pagination-filtering.md).
 * total_count / has_more 포함, 정렬은 각 쿼리에서 기본값 명시.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasMore
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext());
    }

    public static <S, T> PageResponse<T> from(Page<S> page, List<T> mappedContent) {
        return new PageResponse<>(
                mappedContent,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext());
    }
}
