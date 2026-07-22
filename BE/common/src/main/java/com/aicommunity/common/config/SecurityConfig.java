package com.aicommunity.common.config;

import com.aicommunity.common.security.JwtAuthenticationFilter;
import com.aicommunity.common.security.JwtTokenProvider;
import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 모든 백엔드 서비스 공용 보안 설정 (자원 서버).
 * - 무상태(JWT), CSRF off, CORS on.
 * - URL 은 기본 허용(permitAll) 하고, 보호가 필요한 동작은 컨트롤러에서 @PreAuthorize 로 강제한다.
 *   (skills/frontEnd/routing-routing-auth: 최종 강제는 서버. 여기서 메서드 보안으로 재검증.)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider tokenProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // "corsConfigurationSource" 빈(WebCorsConfig)을 이름으로 조회한다.
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // OWASP: 보안 헤더 (nosniff·frame deny·referrer·HSTS·CSP)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .referrerPolicy(rp -> rp.policy(
                                org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
                                        .ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none'")))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    public Clock clock() {
        return Clock.systemUTC();
    }
}
