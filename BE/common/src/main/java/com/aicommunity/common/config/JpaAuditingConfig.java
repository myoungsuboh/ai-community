package com.aicommunity.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 (BaseTimeEntity 의 created_at/updated_at 자동 채움).
 * 각 서비스는 base package "com.aicommunity" 를 스캔하므로 이 설정을 공유한다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
