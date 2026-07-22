package com.aicommunity.useractivity.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, UUID> {
    boolean existsByCommentIdAndReporterId(UUID commentId, UUID reporterId);
}
