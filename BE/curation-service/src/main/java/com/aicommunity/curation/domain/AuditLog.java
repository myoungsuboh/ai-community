package com.aicommunity.curation.domain;

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
 * AuditLog (소속 Aggregate: Card). 모든 카드 발행/수정/반려 이력 기록 (POL-01).
 */
@Entity
@Table(name = "audit_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    private UUID id;

    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Column(name = "modifier_id", nullable = false)
    private UUID modifierId;

    @Column(name = "action_type", nullable = false)
    private String actionType; // PUBLISH | UPDATE | REJECT

    @Column(name = "changed_fields")
    private String changedFields;

    @Column
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static AuditLog of(UUID cardId, UUID modifierId, String actionType,
                              String changedFields, String reason, OffsetDateTime now) {
        AuditLog a = new AuditLog();
        a.id = UUID.randomUUID();
        a.cardId = cardId;
        a.modifierId = modifierId;
        a.actionType = actionType;
        a.changedFields = changedFields;
        a.reason = reason;
        a.createdAt = now;
        return a;
    }
}
