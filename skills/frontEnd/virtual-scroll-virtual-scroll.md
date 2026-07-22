---
name: "가상 스크롤 & 무한 스크롤 (Virtual / Infinite Scroll)"
description: "대량 리스트 렌더링 성능을 위한 가상화와 무한 스크롤 구현 표준. 1000개 이상 리스트를 그리거나 무한 스크롤을 붙일 때 읽는다. 키워드: virtual scroll, virtualization, infinite scroll, IntersectionObserver, vue-virtual-scroller, TanStack Virtual."
---

# 가상 스크롤 & 무한 스크롤 (Virtual / Infinite Scroll)

**ID:** `SKL-VIRTUAL-SCROLL`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 대량 리스트 렌더링 성능을 위한 가상화와 무한 스크롤 구현 표준. 1000개 이상 리스트를 그리거나 무한 스크롤을 붙일 때 읽는다. 키워드: virtual scroll, virtualization, infinite scroll, IntersectionObserver, vue-virtual-scroller, TanStack Virtual.

---

## 지시사항 (Instructions)

1. 1000개 이상의 행은 가상 스크롤(Virtual Scroll)을 필수 적용한다.
2. 직접 구현보다 검증된 라이브러리(vue-virtual-scroller·TanStack Virtual)를 우선 쓴다.
3. 가상 스크롤 컨테이너에 고정 height를 주고, 아이템 예상 높이를 정확히 전달한다.
4. 무한 스크롤은 스크롤 이벤트 대신 IntersectionObserver로 감시하고 로딩·오류·빈 상태를 표시한다.
5. 접근성: aria-live 또는 페이지네이션 대안을 제공한다.

## 태그

`virtual scroll` `virtualization` `infinite scroll` `IntersectionObserver` `vue-virtual-scroller` `TanStack Virtual` `virtual-scroll` `infinite-scroll` `tanstack-virtual` `performance` `frontEnd` `ai-recommended`
