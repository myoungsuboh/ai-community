package com.aicommunity.curation.service;

import com.aicommunity.common.support.HtmlSanitizer;
import com.aicommunity.curation.domain.Post;
import com.aicommunity.curation.domain.PostRepository;
import com.aicommunity.curation.domain.event.CurationEvents;
import com.aicommunity.curation.dto.PostDtos.CreatePostRequest;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public PostService(PostRepository postRepository, ApplicationEventPublisher events, Clock clock) {
        this.postRepository = postRepository;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public Post create(UUID authorId, CreatePostRequest req) {
        // POL-36/POL-35: 허용되지 않는 HTML 제거(XSS 방지)
        String safeTitle = HtmlSanitizer.stripAll(req.title());
        String safeContent = HtmlSanitizer.stripAll(req.content());
        List<String> tags = req.tags() == null ? List.of()
                : req.tags().stream().map(HtmlSanitizer::stripAll).toList();

        Post post = Post.create(safeTitle, safeContent, authorId, tags);
        postRepository.save(post);
        events.publishEvent(new CurationEvents.PostCreated(
                post.getId(), authorId, post.getTitle(), OffsetDateTime.now(clock)));
        return post;
    }

    @Transactional(readOnly = true)
    public Post get(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new com.aicommunity.common.error.BusinessException(
                        com.aicommunity.common.error.ErrorCode.NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<Post> search(String keyword, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100); // 최대 100 (pagination 규칙)
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        // POL-13: 검색어는 1~50자
        if (kw != null && kw.length() > 50) {
            throw new com.aicommunity.common.error.BusinessException(
                    com.aicommunity.common.error.ErrorCode.VALIDATION_FAILED, "검색어는 50자 이내여야 합니다.");
        }
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.search(kw, pageable);
    }
}
