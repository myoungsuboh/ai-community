package com.aicommunity.useractivity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Reaction 애그리거트 (좋아요/북마크). 불변식: 계정당 카드당 타입별 1회(POL-04, uq 제약).
 * 토글 오프는 물리 삭제(토글 성격) — soft-delete 는 콘텐츠성 데이터(comments)에 적용.
 */
@Entity
@Table(name = "reactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reaction {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static Reaction of(UUID userId, UUID cardId, ReactionType type, OffsetDateTime now) {
        Reaction r = new Reaction();
        r.id = UUID.randomUUID();
        r.userId = userId;
        r.cardId = cardId;
        r.type = type;
        r.createdAt = now;
        return r;
    }
}
