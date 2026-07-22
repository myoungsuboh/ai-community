package com.aicommunity.project.domain;

import com.aicommunity.common.entity.BaseTimeEntity;
import com.aicommunity.common.support.StringListJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Project 애그리거트. 불변식(2_ddd.md): 이름 2자↑·중복불가(POL-12), 관리자(owner)만 진행상황 수정.
 */
@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Column
    private String progress;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "text")
    private List<String> tags;

    public static Project create(String name, String description, UUID ownerId, List<String> tags) {
        Project p = new Project();
        p.id = UUID.randomUUID();
        p.name = name;
        p.description = description;
        p.ownerId = ownerId;
        p.status = ProjectStatus.RECRUITING;
        p.tags = tags;
        return p;
    }

    public boolean isOwner(UUID userId) {
        return this.ownerId.equals(userId);
    }

    public void updateInfo(String name, String description, List<String> tags) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (tags != null) this.tags = tags;
    }

    public void updateStatus(ProjectStatus status, String progress) {
        if (status != null) this.status = status;
        if (progress != null) this.progress = progress;
    }
}
