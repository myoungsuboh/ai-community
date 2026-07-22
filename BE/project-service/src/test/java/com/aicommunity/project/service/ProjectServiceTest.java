package com.aicommunity.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicommunity.common.error.BusinessException;
import com.aicommunity.common.error.ErrorCode;
import com.aicommunity.project.domain.Project;
import com.aicommunity.project.domain.ProjectRepository;
import com.aicommunity.project.domain.ProjectStatus;
import com.aicommunity.project.domain.event.ProjectEvents;
import com.aicommunity.project.dto.ProjectDtos.UpdateProjectRequest;
import com.aicommunity.project.dto.ProjectDtos.UpdateStatusRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock ProjectRepository projectRepository;
    @Mock ApplicationEventPublisher events;

    Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);
    ProjectService service;
    final UUID owner = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ProjectService(projectRepository, events, clock);
    }

    @Test
    @DisplayName("생성: 이름 미중복이면 저장 + ProjectCreated")
    void create_ok() {
        when(projectRepository.existsByName("AI 스터디")).thenReturn(false);
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Project p = service.create(owner, "AI 스터디", "설명", List.of("study"));
        assertThat(p.getName()).isEqualTo("AI 스터디");
        assertThat(p.getStatus()).isEqualTo(ProjectStatus.RECRUITING);
        verify(events).publishEvent(any(ProjectEvents.ProjectCreated.class));
    }

    @Test
    @DisplayName("생성: 중복 이름 → PROJECT_NAME_DUPLICATE (POL-12)")
    void create_duplicate() {
        when(projectRepository.existsByName("중복")).thenReturn(true);
        assertThatThrownBy(() -> service.create(owner, "중복", null, null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.PROJECT_NAME_DUPLICATE);
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("수정: 관리자 아니면 FORBIDDEN")
    void update_notOwner() {
        Project p = Project.create("P", "d", owner, List.of());
        when(projectRepository.findById(p.getId())).thenReturn(Optional.of(p));
        UUID other = UUID.randomUUID();
        assertThatThrownBy(() -> service.update(other, p.getId(), new UpdateProjectRequest("새이름", null, null)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("진행상황: 관리자가 상태 변경")
    void updateStatus_owner() {
        Project p = Project.create("P", "d", owner, List.of());
        when(projectRepository.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service.updateStatus(owner, p.getId(), new UpdateStatusRequest("IN_PROGRESS", "1차 완료"));
        assertThat(p.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(p.getProgress()).isEqualTo("1차 완료");
        verify(events).publishEvent(any(ProjectEvents.ProjectUpdated.class));
    }

    @Test
    @DisplayName("진행상황: 잘못된 상태값 → VALIDATION_FAILED")
    void updateStatus_invalid() {
        Project p = Project.create("P", "d", owner, List.of());
        when(projectRepository.findById(p.getId())).thenReturn(Optional.of(p));
        assertThatThrownBy(() -> service.updateStatus(owner, p.getId(), new UpdateStatusRequest("NOPE", null)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.VALIDATION_FAILED);
    }

    @Test
    @DisplayName("조회: 없는 프로젝트 → NOT_FOUND")
    void get_notFound() {
        when(projectRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_FOUND);
    }
}
