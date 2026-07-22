package com.aicommunity.project.service;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.common.support.HtmlSanitizer;
import com.aicommunity.project.domain.Project;
import com.aicommunity.project.domain.ProjectRepository;
import com.aicommunity.project.domain.ProjectStatus;
import com.aicommunity.project.domain.event.ProjectEvents;
import com.aicommunity.project.dto.ProjectDtos.UpdateProjectRequest;
import com.aicommunity.project.dto.ProjectDtos.UpdateStatusRequest;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public ProjectService(ProjectRepository projectRepository, ApplicationEventPublisher events, Clock clock) {
        this.projectRepository = projectRepository;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public Project create(UUID ownerId, String name, String description, List<String> tags) {
        String safeName = HtmlSanitizer.stripAll(name);
        if (projectRepository.existsByName(safeName)) { // POL-12
            throw new BusinessException(ErrorCode.PROJECT_NAME_DUPLICATE);
        }
        List<String> safeTags = tags == null ? List.of() : tags.stream().map(HtmlSanitizer::stripAll).toList();
        Project p = Project.create(safeName, HtmlSanitizer.stripAll(description), ownerId, safeTags);
        projectRepository.save(p);
        events.publishEvent(new ProjectEvents.ProjectCreated(p.getId(), ownerId, p.getName(), OffsetDateTime.now(clock)));
        return p;
    }

    @Transactional
    public Project update(UUID userId, UUID projectId, UpdateProjectRequest req) {
        Project p = ownedProject(userId, projectId);
        String newName = req.name() == null ? null : HtmlSanitizer.stripAll(req.name());
        if (newName != null && !newName.equals(p.getName()) && projectRepository.existsByName(newName)) {
            throw new BusinessException(ErrorCode.PROJECT_NAME_DUPLICATE);
        }
        List<String> safeTags = req.tags() == null ? null : req.tags().stream().map(HtmlSanitizer::stripAll).toList();
        p.updateInfo(newName, req.description() == null ? null : HtmlSanitizer.stripAll(req.description()), safeTags);
        projectRepository.save(p);
        events.publishEvent(new ProjectEvents.ProjectUpdated(p.getId(), userId, "info", OffsetDateTime.now(clock)));
        return p;
    }

    @Transactional
    public Project updateStatus(UUID userId, UUID projectId, UpdateStatusRequest req) {
        Project p = ownedProject(userId, projectId); // 관리자(owner)만 진행상황 수정 (invariant)
        ProjectStatus status = null;
        if (req.status() != null && !req.status().isBlank()) {
            try {
                status = ProjectStatus.valueOf(req.status());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED, "상태 값이 올바르지 않습니다.");
            }
        }
        p.updateStatus(status, req.progress() == null ? null : HtmlSanitizer.stripAll(req.progress()));
        projectRepository.save(p);
        events.publishEvent(new ProjectEvents.ProjectUpdated(p.getId(), userId, "status", OffsetDateTime.now(clock)));
        return p;
    }

    @Transactional(readOnly = true)
    public Page<Project> list(String keyword, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return projectRepository.search(kw,
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public Project get(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
    }

    private Project ownedProject(UUID userId, UUID projectId) {
        Project p = get(projectId);
        if (!p.isOwner(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "프로젝트 관리자만 수정할 수 있어요.");
        }
        return p;
    }
}
