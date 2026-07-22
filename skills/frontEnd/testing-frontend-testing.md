---
name: "프론트엔드 테스트 표준 (Frontend Testing)"
description: "프론트엔드 테스트의 스택 적용 표준: 안정 셀렉터(data-testid)와 프론트 커버리지 임계·CI 게이트, Vitest/Testing Library/Playwright 구현 예시를 다룬다. 범용 개념(피라미드·AAA·격리·모킹 경계·비결정성)은 unit-testing·integration-testing·test-strategy 에 위임한다. 테스트를 작성·정비하거나 테스트 환경·CI 게이트를 구성할 때 읽는다. 키워드: testing, component, e2e, data-test, stable selector, coverage, flaky, vitest, playwright."
---

# 프론트엔드 테스트 표준 (Frontend Testing)

**ID:** `SKL-FRONTEND-TESTING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 프론트엔드 테스트의 스택 적용 표준: 안정 셀렉터(data-testid)와 프론트 커버리지 임계·CI 게이트, Vitest/Testing Library/Playwright 구현 예시를 다룬다. 범용 개념(피라미드·AAA·격리·모킹 경계·비결정성)은 unit-testing·integration-testing·test-strategy 에 위임한다. 테스트를 작성·정비하거나 테스트 환경·CI 게이트를 구성할 때 읽는다. 키워드: testing, component, e2e, data-test, stable selector, coverage, flaky, vitest, playwright.

---

## 지시사항 (Instructions)

1. 클릭·입력 등 상호작용 대상은 표시 텍스트·CSS 구조가 아니라 테스트 전용 안정 셀렉터(data-testid)로 찾는다.
2. 커버리지는 핵심 로직 분기 우선으로 합리적 임계(분기 65~75%)를 정하고, 100% 강요를 금지한다.
3. 린트·단위/컴포넌트·E2E 를 PR 마다 CI 게이트로 실행해 통과해야 머지되게 한다.
4. 단위/컴포넌트 테스트는 Vitest + Testing Library(Vue Test Utils)로 작성하고, 외부 의존은 신뢰 경계에서만 모킹한다.
5. 브라우저 E2E 는 Playwright 로 핵심 사용자 흐름만 얇게 덮는다(피라미드 70/25/5 비율).
6. 간헐 실패(flaky) 테스트는 즉시 격리·수정하고, fake timer·seed 주입으로 비결정성을 제거한다.

## 태그

`testing` `component` `e2e` `data-test` `stable selector` `coverage` `flaky` `vitest` `playwright` `frontend-testing` `frontEnd` `ai-recommended`
