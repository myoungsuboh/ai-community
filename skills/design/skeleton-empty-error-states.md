---
name: "빈 상태 · 오류 · 로딩 상태 UI"
description: "빈 상태(Empty State)·오류(Error)·로딩(Loading) 등 비정상 상태를 일관되게 디자인해 사용자 혼란을 막는 표준. 비동기 화면을 만들거나 상태별 UI를 정할 때 읽는다. 키워드: skeleton, loading, error, empty, retry, spinner, placeholder."
---

# 빈 상태 · 오류 · 로딩 상태 UI

**ID:** `SKL-EMPTY-ERROR-STATES`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 빈 상태(Empty State)·오류(Error)·로딩(Loading) 등 비정상 상태를 일관되게 디자인해 사용자 혼란을 막는 표준. 비동기 화면을 만들거나 상태별 UI를 정할 때 읽는다. 키워드: skeleton, loading, error, empty, retry, spinner, placeholder.

---

## 지시사항 (Instructions)

1. 모든 비동기 UI 영역은 로딩·성공·오류·빈상태 네 가지를 명시적으로 처리한다.
2. 로딩 상태는 실제 콘텐츠 레이아웃과 동일한 Skeleton UI를 사용해 레이아웃 점프를 방지한다.
3. 오류 상태는 재시도 버튼을 포함하고, 전체 페이지 오류와 부분(인라인) 오류를 구분한다.
4. 빈 상태는 단순 'No data'가 아닌 이유·행동 유도·일러스트로 구성한다.
5. 낙관적 업데이트(Optimistic UI) 적용 시 롤백 시나리오와 오류 복구를 반드시 구현한다.

## 태그

`skeleton` `loading` `error` `empty` `retry` `spinner` `placeholder` `empty-error-states` `design` `ai-recommended`
