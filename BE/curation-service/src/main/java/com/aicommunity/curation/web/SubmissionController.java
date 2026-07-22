package com.aicommunity.curation.web;

import com.aicommunity.common.security.UserPrincipal;
import com.aicommunity.common.web.PageResponse;
import com.aicommunity.curation.domain.Submission;
import com.aicommunity.curation.domain.SubmissionStatus;
import com.aicommunity.curation.dto.SubmissionDtos.CreateSubmissionRequest;
import com.aicommunity.curation.dto.SubmissionDtos.ReviewRequest;
import com.aicommunity.curation.dto.SubmissionDtos.ReviewResult;
import com.aicommunity.curation.dto.SubmissionDtos.SubmissionResponse;
import com.aicommunity.curation.dto.SubmissionDtos.SubmissionSummary;
import com.aicommunity.curation.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // URL 제보 (회원)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubmissionResponse> submit(@AuthenticationPrincipal UserPrincipal user,
                                                     @Valid @RequestBody CreateSubmissionRequest req) {
        Submission s = submissionService.submit(user.id(), req.url());
        return ResponseEntity.status(HttpStatus.CREATED).body(SubmissionResponse.from(s));
    }

    // 큐레이터 검수함 목록 (검수함 화면의 읽기측 — 20-API 명세엔 없으나 화면 구현에 필요)
    @GetMapping
    @PreAuthorize("hasAnyRole('CURATOR','ADMIN')")
    public PageResponse<SubmissionSummary> list(
            @RequestParam(defaultValue = "PENDING") SubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Submission> result = submissionService.listByStatus(status, page, size);
        return PageResponse.from(result, result.map(SubmissionSummary::from).getContent());
    }

    // 제보 검수 및 발행/반려 (큐레이터)
    @PatchMapping("/{submissionId}/review")
    @PreAuthorize("hasAnyRole('CURATOR','ADMIN')")
    public ReviewResult review(@AuthenticationPrincipal UserPrincipal user,
                               @PathVariable java.util.UUID submissionId,
                               @Valid @RequestBody ReviewRequest req) {
        return submissionService.review(user.id(), submissionId, req);
    }
}
