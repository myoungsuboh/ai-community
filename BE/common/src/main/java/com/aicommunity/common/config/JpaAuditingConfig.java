package com.aicommunity.common.config;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 (BaseTimeEntity 의 created_at/updated_at 자동 채움).
 * 기본 provider 는 LocalDateTime 을 만들어 OffsetDateTime 필드로 변환하지 못하므로,
 * OffsetDateTime 을 돌려주는 DateTimeProvider 를 명시한다.
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaAuditingConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider(Clock clock) {
        return () -> Optional.<TemporalAccessor>of(OffsetDateTime.now(clock));
    }
}
