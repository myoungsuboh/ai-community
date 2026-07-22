package com.aicommunity.useractivity.service;

import com.aicommunity.useractivity.domain.Card;
import com.aicommunity.useractivity.domain.LibraryCardRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryService {

    private final LibraryCardRepository libraryCardRepository;

    public LibraryService(LibraryCardRepository libraryCardRepository) {
        this.libraryCardRepository = libraryCardRepository;
    }

    @Transactional(readOnly = true)
    public Page<Card> bookmarks(UUID userId, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return libraryCardRepository.findBookmarkedByUser(userId,
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "publishedAt")));
    }
}
