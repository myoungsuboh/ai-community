package com.aicommunity.useractivity.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    Optional<Reaction> findByUserIdAndCardIdAndType(UUID userId, UUID cardId, ReactionType type);

    boolean existsByUserIdAndCardIdAndType(UUID userId, UUID cardId, ReactionType type);
}
