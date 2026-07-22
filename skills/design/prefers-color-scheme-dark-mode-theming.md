---
name: "다크모드 & 테마 시스템"
description: "CSS Custom Properties와 prefers-color-scheme으로 라이트/다크 모드 및 확장 가능한 테마를 구현하는 가이드. 새 화면에 색을 입히거나 테마 토글·시스템 테마 감지·다크모드 색을 정할 때 읽는다. 키워드: prefers-color-scheme, data-theme, color-scheme, dark, localStorage, currentColor, --color-."
---

# 다크모드 & 테마 시스템

**ID:** `SKL-DARK-MODE-THEMING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** CSS Custom Properties와 prefers-color-scheme으로 라이트/다크 모드 및 확장 가능한 테마를 구현하는 가이드. 새 화면에 색을 입히거나 테마 토글·시스템 테마 감지·다크모드 색을 정할 때 읽는다. 키워드: prefers-color-scheme, data-theme, color-scheme, dark, localStorage, currentColor, --color-.

---

## 지시사항 (Instructions)

1. 색상은 반드시 CSS Custom Properties(시맨틱 토큰)로만 참조하고, 라이트/다크 값은 해당 스코프에서 재정의한다.
2. prefers-color-scheme 으로 시스템 테마를 자동 감지하되, 사용자 수동 선택(light/dark/system)도 지원한다.
3. 다크모드에서 순수 검정(#000000) 대신 어두운 회색 계열(#0f172a 등)을 써 눈 피로를 줄인다.
4. 이미지·동영상은 다크모드에서 과하게 밝을 때만 필터를 케이스별로 적용하고(고채도 사진은 별도 에셋 고려), SVG는 currentColor 를 쓴다.
5. 테마 선택은 localStorage에 저장하고 초기 로딩 시 깜빡임(FOUC)을 방지한다.

## 태그

`prefers-color-scheme` `data-theme` `color-scheme` `dark` `localStorage` `currentColor` `--color-` `dark-mode-theming` `design` `ai-recommended`
