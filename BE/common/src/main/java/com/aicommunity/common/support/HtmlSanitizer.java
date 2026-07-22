package com.aicommunity.common.support;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * 게시글 등 사용자 입력의 HTML 정화 (POL-36: 허용되지 않는 태그 제거/이스케이프, POL-35 XSS 방지).
 * 기본 정책: 모든 태그 제거(plain text) — 커뮤니티 텍스트 콘텐츠에 안전한 최소 정책.
 */
public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    /** 모든 HTML 태그를 제거하고 텍스트만 남긴다(엔티티 디코드 포함). */
    public static String stripAll(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = Jsoup.clean(input, Safelist.none());
        // Jsoup 이 남긴 엔티티(&amp; 등)를 사람이 읽는 텍스트로 되돌린다.
        return Jsoup.parse(cleaned).text();
    }
}
