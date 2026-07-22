package com.aicommunity.curation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * curation-service — 진입점. base package "com.aicommunity" 를 스캔해 공용(common) 설정을 함께 로드한다.
 */
@SpringBootApplication(scanBasePackages = "com.aicommunity")
public class CurationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurationServiceApplication.class, args);
    }
}
