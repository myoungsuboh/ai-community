package com.aicommunity.useractivity.web;

import com.aicommunity.common.security.UserPrincipal;
import com.aicommunity.common.web.PageResponse;
import com.aicommunity.useractivity.domain.Comment;
import com.aicommunity.useractivity.dto.ActivityDtos.CommentResponse;
import com.aicommunity.useractivity.dto.ActivityDtos.CreateCommentRequest;
import com.aicommunity.useractivity.dto.ActivityDtos.ReportResult;
import com.aicommunity.useractivity.dto.ActivityDtos.VisibilityRequest;
import com.aicommunity.useractivity.service.CommentService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 작성 (회원)
    @PostMapping("/api/v1/cards/{cardId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> create(@AuthenticationPrincipal UserPrincipal user,
                                                  @PathVariable UUID cardId,
                                                  @Valid @RequestBody CreateCommentRequest req) {
        Comment c = commentService.create(user.id(), cardId, req.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(c));
    }

    // 카드 댓글 목록 (공개) — 카드 상세 화면 표시용 (20-API 명세엔 없으나 화면 구현에 필요)
    @GetMapping("/api/v1/cards/{cardId}/comments")
    public PageResponse<CommentResponse> list(@PathVariable UUID cardId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        Page<Comment> result = commentService.list(cardId, page, size);
        return PageResponse.from(result, result.map(CommentResponse::from).getContent());
    }

    // 댓글 신고 (회원) — 사유 없이 신고만
    @PostMapping("/api/v1/comments/{commentId}/report")
    @PreAuthorize("isAuthenticated()")
    public ReportResult report(@AuthenticationPrincipal UserPrincipal user, @PathVariable UUID commentId) {
        return commentService.report(user.id(), commentId);
    }

    // 댓글 숨김/해제 (큐레이터)
    @PatchMapping("/api/v1/comments/{commentId}/visibility")
    @PreAuthorize("hasAnyRole('CURATOR','ADMIN')")
    public CommentResponse setVisibility(@AuthenticationPrincipal UserPrincipal user,
                                         @PathVariable UUID commentId,
                                         @Valid @RequestBody VisibilityRequest req) {
        return CommentResponse.from(commentService.setVisibility(user.id(), commentId, req.hidden()));
    }
}
