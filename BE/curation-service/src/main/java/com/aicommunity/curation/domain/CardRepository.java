package com.aicommunity.curation.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
