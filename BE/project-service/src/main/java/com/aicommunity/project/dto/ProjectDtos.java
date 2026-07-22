package com.aicommunity.project.dto;

import com.aicommunity.project.domain.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class ProjectDtos {

    private ProjectDtos() {
    }

    // POL-12: 프로젝트명 2자 이상
    public record CreateProjectRequest(
            @NotBlank(message = "프로젝트명을 입력해 주세요.") @Size(min = 2, max = 100, message = "프로젝트명은 2자 이상이어야 합니다.") String name,
            String description,
            List<String> tags) {
    }

    public record UpdateProjectRequest(
            @Size(min = 2, max = 100, message = "프로젝트명은 2자 이상이어야 합니다.") String name,
            String description,
            List<String> tags) {
    }

    public record UpdateStatusRequest(String status, String progress) {
    }

    public record ProjectSummary(UUID projectId, String name, UUID ownerId, String status,
                                 List<String> tags, OffsetDateTime createdAt) {
        public static ProjectSummary from(Project p) {
            return new ProjectSummary(p.getId(), p.getName(), p.getOwnerId(), p.getStatus().name(),
                    p.getTags() == null ? List.of() : p.getTags(), p.getCreatedAt());
        }
    }

    public record ProjectResponse(UUID projectId, String name, String description, UUID ownerId,
                                  String status, String progress, List<String> tags,
                                  OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        public static ProjectResponse from(Project p) {
            return new ProjectResponse(p.getId(), p.getName(), p.getDescription(), p.getOwnerId(),
                    p.getStatus().name(), p.getProgress(), p.getTags() == null ? List.of() : p.getTags(),
                    p.getCreatedAt(), p.getUpdatedAt());
        }
    }
}
