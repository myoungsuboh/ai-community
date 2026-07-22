---
name: "인덱싱 전략 (Indexing Strategy)"
description: "인덱스 설계·유지 관리의 범용 표준이다: 인덱스 유형 선택, 복합 컬럼 순서, 커버링, 실행계획(EXPLAIN) 확인, 미사용 인덱스 정리를 다룬다. 새 인덱스를 만들거나 느린 쿼리를 튜닝하고 인덱스 순서·미사용 인덱스를 점검할 때 읽는다. 키워드: index, composite-index, covering-index, explain, unused-index."
---

# 인덱싱 전략 (Indexing Strategy)

**ID:** `SKL-INDEXING-STRATEGY`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 인덱스 설계·유지 관리의 범용 표준이다: 인덱스 유형 선택, 복합 컬럼 순서, 커버링, 실행계획(EXPLAIN) 확인, 미사용 인덱스 정리를 다룬다. 새 인덱스를 만들거나 느린 쿼리를 튜닝하고 인덱스 순서·미사용 인덱스를 점검할 때 읽는다. 키워드: index, composite-index, covering-index, explain, unused-index.

---

## 지시사항 (Instructions)

1. 인덱스는 읽기를 빠르게, 쓰기를 느리게 한다: 모든 쓰기(INSERT/UPDATE/DELETE)는 인덱스도 함께 갱신한다. WHERE·JOIN·ORDER BY에 실제로 쓰이는 컬럼에 집중하고, 과잉 생성을 피한다.
2. 측정 후 설계한다: 추측으로 인덱스를 만들지 말고, 실행계획(EXPLAIN류)으로 느린 접근(풀스캔 등)을 먼저 확인한 뒤 인덱스를 추가하고, 다시 실행계획으로 효과를 검증한다.
3. 복합 인덱스는 컬럼 순서가 전부다: 선택도(cardinality)가 높은 컬럼과 등가(=) 조건 컬럼을 앞에, 범위(>,<,BETWEEN)·정렬 컬럼을 뒤에 둔다. 인덱스는 '왼쪽 접두사(leftmost prefix)'만 활용되므로 쿼리 조건 순서와 맞춘다.
4. 컬럼을 가공하면 인덱스를 못 탄다: WHERE 절에서 컬럼에 함수·연산을 씌우거나(UPPER(col), col + 1) 앞부분이 와일드카드인 패턴('%suffix')을 쓰면 인덱스가 무력화된다: 식 자체를 인덱싱하거나 쿼리를 바꾼다.
5. 꼭 필요한 만큼만, 조건부로 좁힌다: 일부 행만 조회 대상이면 조건부(부분) 인덱스로 크기를 줄이고, 조회에 필요한 컬럼을 인덱스에 포함(커버링)하면 테이블 접근 없이 인덱스만으로 응답할 수 있다.
6. 미사용 인덱스는 부채다: 한 번도 사용되지 않는 인덱스는 쓰기 비용·저장 공간만 잡아먹는다. 사용 통계를 주기적으로 확인해 제거한다.
7. 운영 테이블 인덱스 생성은 락을 조심한다: 큰 운영 테이블에 인덱스를 만들면 쓰기 락이 걸려 장애가 될 수 있다: DB가 제공하는 온라인/무중단 인덱스 생성 옵션을 쓴다.

## 태그

`index` `composite-index` `covering-index` `explain` `unused-index` `b-tree` `partial-index` `cardinality` `indexing-strategy` `db` `ai-recommended`
