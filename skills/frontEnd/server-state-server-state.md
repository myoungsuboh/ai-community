---
name: "서버 상태 · 데이터 패칭 (Server State & Data Fetching)"
description: "서버가 소유한 비동기 데이터를 전용 캐시 계층(데이터 패칭 라이브러리)으로 다루는 스택 중립 표준: 클라이언트 상태와 분리, 로딩/에러/빈 처리, 쿼리 키 캐시·중복요청 제거, 변경 후 무효화, 낙관적 업데이트+롤백. 목록·상세를 불러오거나 캐시·갱신·동기화하거나, 전역 store에 API 응답을 쌓고 있을 때 읽는다. 키워드: server-state, data-fetching, cache, invalidation, optimistic-update, stale-while-revalidate, TanStack-Query, SWR."
---

# 서버 상태 · 데이터 패칭 (Server State & Data Fetching)

**ID:** `SKL-SERVER-STATE`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 서버가 소유한 비동기 데이터를 전용 캐시 계층(데이터 패칭 라이브러리)으로 다루는 스택 중립 표준: 클라이언트 상태와 분리, 로딩/에러/빈 처리, 쿼리 키 캐시·중복요청 제거, 변경 후 무효화, 낙관적 업데이트+롤백. 목록·상세를 불러오거나 캐시·갱신·동기화하거나, 전역 store에 API 응답을 쌓고 있을 때 읽는다. 키워드: server-state, data-fetching, cache, invalidation, optimistic-update, stale-while-revalidate, TanStack-Query, SWR.

---

## 지시사항 (Instructions)

1. 서버 상태 ≠ 클라이언트 상태: 서버 데이터는 비동기·공유·캐시·만료되는 별개 범주다. 전역 클라 store에 수동 복사하지 말고 데이터 패칭 계층으로 다룬다.
2. 모든 비동기 데이터는 로딩·에러·빈 상태를 가진다: 성공만 가정하지 않는다. 특히 에러와 빈 결과를 항상 처리한다.
3. 캐시는 키로 식별한다: 쿼리 키(자원+파라미터)로 캐시를 정규화하면 같은 데이터의 중복 요청이 자동으로 합쳐진다.
4. stale-while-revalidate: 캐시된 값을 즉시 보여주고 백그라운드에서 갱신한다. 사용자는 빈 화면 대신 (조금 낡았어도) 즉시 데이터를 본다.
5. 변경 후에는 무효화로 동기화: 쓰기(mutation) 성공 시 관련 쿼리를 무효화해 서버를 진실원(SoT)으로 다시 맞춘다. 로컬 배열을 손으로 고치지 않는다.
6. 낙관적 업데이트는 롤백과 한 세트: 즉시 UI를 바꾸려면 실패 시 이전 상태로 되돌릴 수 있어야 한다.

## 태그

`server-state` `data-fetching` `cache` `invalidation` `optimistic-update` `stale-while-revalidate` `TanStack-Query` `SWR` `frontEnd` `ai-recommended`
