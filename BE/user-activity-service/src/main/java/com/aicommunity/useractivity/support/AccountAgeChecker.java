package com.aicommunity.useractivity.support;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * POL-15: 가입 후 24시간이 지나지 않은 계정의 좋아요/북마크/댓글은 커뮤니티 점수 계산에서 제외.
 * users 테이블(공유 DB)의 created_at 을 읽어 판정한다.
 */
@Component
public class AccountAgeChecker {

    private final JdbcTemplate jdbc;
    private final Clock clock;

    public AccountAgeChecker(JdbcTemplate jdbc, Clock clock) {
        this.jdbc = jdbc;
        this.clock = clock;
    }

    /** 커뮤니티 점수 계산에 반영해도 되는 계정인가 (가입 24시간 경과). */
    public boolean eligibleForScoring(UUID userId) {
        try {
            OffsetDateTime createdAt = jdbc.queryForObject(
                    "SELECT created_at FROM users WHERE id = ?",
                    (rs, i) -> rs.getObject("created_at", OffsetDateTime.class),
                    userId);
            if (createdAt == null) {
                return false;
            }
            return !createdAt.isAfter(OffsetDateTime.now(clock).minus(Duration.ofHours(24)));
        } catch (Exception e) {
            return false; // 알 수 없는 계정은 점수 미반영(보수적)
        }
    }
}
