---
name: "Vue 3 + Vuetify 3 UI 개발 표준 (UI Vuetify)"
description: "Vue 3 + Vuetify 3 기반 화면 컴포넌트 생성 표준입니다. 그리드 레이아웃 계층, PascalCase 컴포넌트 네이밍, 반응형 속성, 유틸리티 클래스 사용 규칙을 정의하며 화면 SFC를 생성하거나 리뷰할 때 읽습니다. 키워드 Vuetify, VContainer, VRow, VCol, PascalCase, 그리드."
---

# Vue 3 + Vuetify 3 UI 개발 표준 (UI Vuetify)

**ID:** `SKL-UI-VUETIFY`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** Vue 3 + Vuetify 3 기반 화면 컴포넌트 생성 표준입니다. 그리드 레이아웃 계층, PascalCase 컴포넌트 네이밍, 반응형 속성, 유틸리티 클래스 사용 규칙을 정의하며 화면 SFC를 생성하거나 리뷰할 때 읽습니다. 키워드 Vuetify, VContainer, VRow, VCol, PascalCase, 그리드.

---

## 지시사항 (Instructions)

1. 레이아웃은 반드시 VContainer > VRow > VCol 계층 구조를 준수한다.
2. 모든 Vuetify 컴포넌트는 PascalCase(<V...>)로 작성하고 케밥 케이스는 금지한다.
3. 모든 VCol은 cols='12'와 함께 최소 하나 이상의 반응형 속성(md, lg 등)을 명시한다.
4. 인라인 스타일 대신 Vuetify 유틸리티 클래스(ma, pa 등)를 사용한다.
5. 동일 요구사항에 대해 항상 PascalCase 기반의 표준 그리드 구조를 출력한다.

## 태그

`vuetify` `VBtn` `VCard` `VDialog` `VDataTable` `v-btn` `v-card` `v-dialog` `$vuetify` `createVuetify` `ui-vuetify` `frontEnd` `ai-recommended`
