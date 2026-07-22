package com.aicommunity.project.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/** 프로젝트 도메인 이벤트 (2_ddd.md EVT-04, EVT-05). 인프로세스 발행. */
public final class ProjectEvents {

    private ProjectEvents() {
    }

    public record ProjectCreated(UUID projectId, UUID creatorId, String name, OffsetDateTime createdAt) {
    }

    public record ProjectUpdated(UUID projectId, UUID modifierId, String updatedFields, OffsetDateTime updatedAt) {
    }
}
