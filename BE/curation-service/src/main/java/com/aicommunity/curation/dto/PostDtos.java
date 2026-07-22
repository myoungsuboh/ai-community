package com.aicommunity.curation.dto;

import com.aicommunity.curation.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class PostDtos {

    private PostDtos() {
    }

    // POL-06: 제목 5자↑, 내용 10자↑
    public record CreatePostRequest(
            @NotBlank @Size(min = 5, message = "제목은 5자 이상이어야 합니다.") String title,
            @NotBlank @Size(min = 10, message = "내용은 10자 이상이어야 합니다.") String content,
            List<String> tags) {
    }

    public record PostResponse(UUID postId, String title, String content, UUID authorId,
                               List<String> tags, OffsetDateTime createdAt) {
        public static PostResponse from(Post p) {
            return new PostResponse(p.getId(), p.getTitle(), p.getContent(), p.getAuthorId(),
                    p.getTags() == null ? List.of() : p.getTags(), p.getCreatedAt());
        }
    }

    public record PostSummary(UUID postId, String title, UUID authorId, List<String> tags,
                              OffsetDateTime createdAt) {
        public static PostSummary from(Post p) {
            return new PostSummary(p.getId(), p.getTitle(), p.getAuthorId(),
                    p.getTags() == null ? List.of() : p.getTags(), p.getCreatedAt());
        }
    }
}
