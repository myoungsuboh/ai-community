---
name: "쿼리 최적화"
description: "N+1 탐지, 슬로우 쿼리 분석, 쿼리 리팩토링, 통계 관리 표준(DB 중립). 느린 쿼리를 잡거나 실행 계획을 개선할 때, N+1을 제거하거나 인덱스가 안 먹는 원인을 찾을 때 읽는다. 키워드: slow-query, n+1, explain, query-plan, join, batch, select-star, analyze."
---

# 쿼리 최적화

**ID:** `SKL-QUERY-OPTIMIZATION`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** N+1 탐지, 슬로우 쿼리 분석, 쿼리 리팩토링, 통계 관리 표준(DB 중립). 느린 쿼리를 잡거나 실행 계획을 개선할 때, N+1을 제거하거나 인덱스가 안 먹는 원인을 찾을 때 읽는다. 키워드: slow-query, n+1, explain, query-plan, join, batch, select-star, analyze.

---

## 지시사항 (Instructions)

1. N+1 쿼리를 탐지해 JOIN 또는 배치 로딩(IN 절·DataLoader)으로 교체한다.
2. 슬로우 쿼리 로그 임계값을 설정하고(예: 1초 이상), 주기적으로 검토·최적화한다.
3. SELECT *를 피하고 필요한 컬럼만 명시해 I/O와 네트워크 전송을 줄인다.
4. 조인 조건의 컬럼 타입이 일치하는지 확인한다: 타입 불일치는 묵시적 캐스팅으로 인덱스를 무효화한다.
5. 테이블 통계(ANALYZE)를 주기적으로 갱신하고, 플래너가 잘못된 실행 계획을 선택하면 통계 업데이트를 먼저 시도한다.

## 태그

`slow-query` `n+1` `explain` `query-plan` `join` `batch` `select-star` `analyze` `query-optimization` `db` `ai-recommended`
