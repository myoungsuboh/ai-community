package com.aicommunity.curation.web;

import com.aicommunity.common.security.UserPrincipal;
import com.aicommunity.common.web.PageResponse;
import com.aicommunity.curation.domain.Post;
import com.aicommunity.curation.dto.PostDtos.CreatePostRequest;
import com.aicommunity.curation.dto.PostDtos.PostResponse;
import com.aicommunity.curation.dto.PostDtos.PostSummary;
import com.aicommunity.curation.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponse> create(@AuthenticationPrincipal UserPrincipal user,
                                               @Valid @RequestBody CreatePostRequest req) {
        Post post = postService.create(user.id(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(PostResponse.from(post));
    }

    // 게시글 상세 (공개) — 게시글 상세 화면의 읽기측 (20-API 명세엔 없으나 화면 구현에 필요)
    @GetMapping("/{postId}")
    public PostResponse get(@org.springframework.web.bind.annotation.PathVariable java.util.UUID postId) {
        return PostResponse.from(postService.get(postId));
    }

    @GetMapping
    public PageResponse<PostSummary> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Post> result = postService.search(keyword, page, size);
        return PageResponse.from(result, result.map(PostSummary::from).getContent());
    }
}
