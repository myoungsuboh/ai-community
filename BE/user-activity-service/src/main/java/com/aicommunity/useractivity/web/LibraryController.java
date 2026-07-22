package com.aicommunity.useractivity.web;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.common.security.UserPrincipal;
import com.aicommunity.common.web.PageResponse;
import com.aicommunity.useractivity.domain.Card;
import com.aicommunity.useractivity.dto.ActivityDtos.LibraryCard;
import com.aicommunity.useractivity.service.LibraryService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // 내 서재 조회 (본인만)
    @GetMapping("/api/v1/users/{userId}/bookmarks")
    @PreAuthorize("isAuthenticated()")
    public PageResponse<LibraryCard> bookmarks(@AuthenticationPrincipal UserPrincipal user,
                                               @PathVariable UUID userId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "12") int size) {
        if (!user.id().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 서재만 조회할 수 있어요.");
        }
        Page<Card> result = libraryService.bookmarks(userId, page, size);
        return PageResponse.from(result, result.map(LibraryCard::from).getContent());
    }
}
