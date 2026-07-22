package com.aicommunity.curation.domain;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("""
            SELECT p FROM Post p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Post> search(@Param("keyword") String keyword, Pageable pageable);
}
