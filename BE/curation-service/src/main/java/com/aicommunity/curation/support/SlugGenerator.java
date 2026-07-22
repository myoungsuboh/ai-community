package com.aicommunity.curation.support;

import java.util.UUID;

/** 카드 slug 생성: 제목의 ASCII 부분 + 랜덤 접미사(고유성). 한글 등은 접미사로 대체. */
public final class SlugGenerator {

    private SlugGenerator() {
    }

    public static String generate(String title) {
        String base = title == null ? "" : title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (base.length() < 2) {
            base = "card";
        }
        if (base.length() > 100) {
            base = base.substring(0, 100).replaceAll("-$", "");
        }
        String suffix = UUID.randomUUID().toString().substring(0, 6);
        return base + "-" + suffix;
    }
}
