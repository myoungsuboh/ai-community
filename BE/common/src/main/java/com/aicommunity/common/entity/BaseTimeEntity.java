package com.aicommunity.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 모든 엔티티가 상속하는 감사(audit) 컬럼 기반 클래스.
 * skills/db/er-modeling·soft-delete-audit: created_at/updated_at 표준화.
 *
 * UUID 를 애플리케이션에서 직접 할당하므로 Spring Data 의 save() 가 merge(select 후 insert)로
 * 동작하는 것을 막기 위해 Persistable 을 구현한다 — persist 로 동작하게 해 감사필드가 즉시 채워지고
 * 불필요한 SELECT 를 없앤다. isNew 판정은 created_at 미할당(=아직 영속 전) 여부로 한다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity implements Persistable<UUID> {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** 각 엔티티의 @Id getter(Lombok 생성)가 구현한다. */
    @Override
    public abstract UUID getId();

    @Override
    @Transient
    public boolean isNew() {
        return createdAt == null;
    }
}
