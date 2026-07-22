---
name: "폼 검증 (Forms Validation)"
description: "클라이언트 폼 검증의 범용 표준: 복잡도별 도구 선택, 동기·비동기 검증, 디바운스, 제출 중 비활성화, 서버 422 필드 매핑, 메시지 i18n. 특정 프레임워크에 무관하다. 폼 검증을 추가·정비하거나 비동기 중복 체크·서버 검증 오류 표시·검증 메시지 다국어화를 구현할 때, 검증 도구를 고를 때 읽는다. 키워드: form validation, client-side, sync, async, debounce, submit, 422, field error, i18n, schema, cross-field. (서버측 입력 검증은 validation-bean 참조)"
---

# 폼 검증 (Forms Validation)

**ID:** `SKL-FORMS-VALIDATION`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 클라이언트 폼 검증의 범용 표준: 복잡도별 도구 선택, 동기·비동기 검증, 디바운스, 제출 중 비활성화, 서버 422 필드 매핑, 메시지 i18n. 특정 프레임워크에 무관하다. 폼 검증을 추가·정비하거나 비동기 중복 체크·서버 검증 오류 표시·검증 메시지 다국어화를 구현할 때, 검증 도구를 고를 때 읽는다. 키워드: form validation, client-side, sync, async, debounce, submit, 422, field error, i18n, schema, cross-field. (서버측 입력 검증은 validation-bean 참조)

---

## 지시사항 (Instructions)

1. 클라이언트 검증은 UX 보조다: 빠른 피드백으로 사용자를 돕는 용도일 뿐, 보안·정합성의 근거가 아니다. 실제 강제는 반드시 서버에서 한다 (validation-bean 참조).
2. 복잡도에 맞는 도구를 고른다: 필드 몇 개의 정적 규칙이면 프레임워크 내장 규칙으로 충분하고, 다단계·필드 간 상호 의존·비동기가 얽히면 스키마 기반 폼 라이브러리를 쓴다. 작은 폼에 무거운 도구를 끌어오지 않는다.
3. 제약은 선언적 스키마로: 필수·길이·범위·형식 같은 제약을 핸들러 코드에 흩뿌리지 말고 입력 모델/스키마에 선언해 한곳에서 읽히게 한다.
4. 실시간 피드백 + 비동기는 빈도 제어: 동기 규칙은 입력 즉시 피드백을 주되, 서버를 때리는 비동기 검증(중복 체크 등)은 포커스 아웃 시점이나 디바운스로 호출을 줄인다.
5. 제출 흐름을 잠근다: 제출 중에는 버튼을 비활성/로딩 상태로 두어 중복 제출을 막고, 검증을 통과한 값만 보낸다.
6. 서버 검증 오류를 필드로 되돌린다: 서버가 돌려준 필드별 검증 오류(예: 4xx/422)는 해당 입력 옆에 매핑해 보여 준다. 토스트/알림 한 줄로 뭉뚱그리지 않는다.
7. 메시지는 i18n 카탈로그로: 검증 문구를 하드코딩하지 말고 메시지 키로 분리해 다국어·문구 변경에 대응한다.

## 태그

`form validation` `client-side` `sync` `async` `debounce` `submit` `422` `field error` `i18n` `schema` `cross-field. (서버측 입력 검증은 validation-bean 참조)` `v-model` `vuelidate` `zod` `yup` `validator` `required` `forms-validation` `frontEnd` `ai-recommended`
