package com.aicommunity.useractivity.domain;

import com.aicommunity.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Comment 애그리거트. 불변식: 최대 500자(POL-07), 분당 3건(POL-02), 신고 3건 자동숨김(POL-18).
 * soft-delete: deleted_at (NULL=활성).
 */
@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "report_count", nullable = false)
    private int reportCount;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public static Comment create(UUID cardId, UUID userId, String content) {
        Comment c = new Comment();
        c.id = UUID.randomUUID();
        c.cardId = cardId;
        c.userId = userId;
        c.content = content;
        c.reportCount = 0;
        c.hidden = false;
        return c;
    }

    /** 신고 1건 반영. 3건 도달 시 자동 숨김(POL-18) → true 반환. */
    public boolean addReport() {
        this.reportCount++;
        if (this.reportCount >= 3 && !this.hidden) {
            this.hidden = true;
            return true;
        }
        return false;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
