package com.aicommunity.common.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 구조화 요청 로깅 & 상관관계 ID (skills/backEnd/structured-logging-logging-observability.md).
 * 요청마다 requestId 를 MDC 에 넣어 로그를 묶고, 완료 시 method·uri·status·소요시간을 남긴다.
 * actuator 헬스체크는 소음이라 제외.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("REQUEST");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            String uri = request.getRequestURI();
            if (!uri.startsWith("/actuator")) {
                log.info("method={} uri={} status={} durationMs={}",
                        request.getMethod(), uri, response.getStatus(), System.currentTimeMillis() - start);
            }
            MDC.remove("requestId");
        }
    }
}
