---
name: "반응형 스타일 표준 (Responsive Styling)"
description: "반응형 원칙은 `responsive-layout`(SoT)을 따르고, 여기서는 그 표준을 스택(Vue 3 + Vuetify)에 매핑하는 구현만 다룬다: Vuetify 브레이크포인트·VRow/VCol 그리드·spacing 유틸. 화면을 Vuetify로 반응형 구성하거나 브레이크포인트·여백·폰트 크기를 정할 때 읽는다. 키워드: responsive, mobile-first, breakpoints, clamp, grid, viewport, touch target, Vuetify, VRow, VCol."
---

# 반응형 스타일 표준 (Responsive Styling)

**ID:** `SKL-RESPONSIVE-STYLING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 반응형 원칙은 `responsive-layout`(SoT)을 따르고, 여기서는 그 표준을 스택(Vue 3 + Vuetify)에 매핑하는 구현만 다룬다: Vuetify 브레이크포인트·VRow/VCol 그리드·spacing 유틸. 화면을 Vuetify로 반응형 구성하거나 브레이크포인트·여백·폰트 크기를 정할 때 읽는다. 키워드: responsive, mobile-first, breakpoints, clamp, grid, viewport, touch target, Vuetify, VRow, VCol.

---

## 지시사항 (Instructions)

1. 레이아웃은 정의된 브레이크포인트 기준으로 분기한다
2. 폰트 크기는 clamp 기반 유동 타이포그래피로 설정한다
3. 간격은 디자인 토큰의 spacing 단위를 사용한다
4. 모바일 우선으로 작성하고 큰 화면을 점진 확장한다

## 태그

`responsive` `mobile-first` `breakpoints` `clamp` `grid` `viewport` `touch target` `Vuetify` `VRow` `VCol` `@media` `breakpoint` `min-width` `max-width` `grid-template` `flex-wrap` `responsive-styling` `frontEnd` `ai-recommended`
