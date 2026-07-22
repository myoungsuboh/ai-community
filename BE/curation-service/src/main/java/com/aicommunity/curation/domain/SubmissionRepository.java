package com.aicommunity.curation.domain;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    long countBySubmitterIdAndCreatedAtAfter(UUID submitterId, OffsetDateTime after);

    boolean existsByUrlAndStatusIn(String url, Collection<SubmissionStatus> statuses);

    Page<Submission> findByStatus(SubmissionStatus status, Pageable pageable);
}
