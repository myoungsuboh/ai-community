package com.aicommunity.project.web;

import com.aicommunity.common.security.UserPrincipal;
import com.aicommunity.common.web.PageResponse;
import com.aicommunity.project.domain.Project;
import com.aicommunity.project.dto.ProjectDtos.CreateProjectRequest;
import com.aicommunity.project.dto.ProjectDtos.ProjectResponse;
import com.aicommunity.project.dto.ProjectDtos.ProjectSummary;
import com.aicommunity.project.dto.ProjectDtos.UpdateProjectRequest;
import com.aicommunity.project.dto.ProjectDtos.UpdateStatusRequest;
import com.aicommunity.project.service.ProjectService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 프로젝트 생성 (회원)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponse> create(@AuthenticationPrincipal UserPrincipal user,
                                                  @Valid @RequestBody CreateProjectRequest req) {
        Project p = projectService.create(user.id(), req.name(), req.description(), req.tags());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResponse.from(p));
    }

    // 프로젝트 수정 (관리자)
    @PutMapping("/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ProjectResponse update(@AuthenticationPrincipal UserPrincipal user,
                                  @PathVariable UUID projectId,
                                  @Valid @RequestBody UpdateProjectRequest req) {
        return ProjectResponse.from(projectService.update(user.id(), projectId, req));
    }

    // 프로젝트 진행 상황 업데이트 (관리자)
    @PatchMapping("/{projectId}/status")
    @PreAuthorize("isAuthenticated()")
    public ProjectResponse updateStatus(@AuthenticationPrincipal UserPrincipal user,
                                        @PathVariable UUID projectId,
                                        @RequestBody UpdateStatusRequest req) {
        return ProjectResponse.from(projectService.updateStatus(user.id(), projectId, req));
    }

    // 프로젝트 목록 (공개) — 목록 화면용 (20-API 명세엔 없으나 화면 구현에 필요)
    @GetMapping
    public PageResponse<ProjectSummary> list(@RequestParam(required = false) String keyword,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        Page<Project> result = projectService.list(keyword, page, size);
        return PageResponse.from(result, result.map(ProjectSummary::from).getContent());
    }

    // 프로젝트 상세 (공개)
    @GetMapping("/{projectId}")
    public ProjectResponse detail(@PathVariable UUID projectId) {
        return ProjectResponse.from(projectService.get(projectId));
    }
}
