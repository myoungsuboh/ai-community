package com.aicommunity.useractivity.dto;

import com.aicommunity.useractivity.domain.Card;
import com.aicommunity.useractivity.domain.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class ActivityDtos {

    private ActivityDtos() {
    }

    public record ToggleReactionRequest(@NotNull ReactionTypeDto type) {
    }

    public enum ReactionTypeDto { LIKE, BOOKMARK }

    public record ToggleReactionResult(String type, boolean isAdded) {
    }

    // POL-07: 최대 500자
    public record CreateCommentRequest(
            @NotBlank(message = "댓글 내용을 입력해 주세요.") @Size(max = 500, message = "댓글은 최대 500자입니다.") String content) {
    }

    public record CommentResponse(UUID commentId, UUID cardId, UUID userId, String content,
                                  boolean hidden, OffsetDateTime createdAt) {
        public static CommentResponse from(Comment c) {
            return new CommentResponse(c.getId(), c.getCardId(), c.getUserId(), c.getContent(),
                    c.isHidden(), c.getCreatedAt());
        }
    }

    public record ReportResult(UUID commentId, int reportCount, boolean hidden) {
    }

    public record VisibilityRequest(@NotNull Boolean hidden) {
    }

    /** 내 서재 카드 항목 (반려 상태 포함 — FE 가 POL-22 메시지 표시). */
    public record LibraryCard(UUID cardId, String slug, String title, String category, int scoreTotal,
                              String status, int likeCount, int bookmarkCount, int commentCount,
                              boolean sourceAccessible, OffsetDateTime publishedAt) {
        public static LibraryCard from(Card c) {
            return new LibraryCard(c.getId(), c.getSlug(), c.getTitle(), c.getCategory(), c.getScoreTotal(),
                    c.getStatus() == null ? null : c.getStatus().name(), c.getLikeCount(), c.getBookmarkCount(),
                    c.getCommentCount(), c.isSourceAccessible(), c.getPublishedAt());
        }
    }
}
