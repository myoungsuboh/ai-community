package com.aicommunity.useractivity.domain;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // POL-02 분당 3건: 최근 1분 내 작성 수
    long countByUserIdAndCreatedAtAfter(UUID userId, OffsetDateTime after);

    // 공개 목록: 삭제 안 됨 + 숨김 아님
    Page<Comment> findByCardIdAndHiddenFalseAndDeletedAtIsNull(UUID cardId, Pageable pageable);
}
