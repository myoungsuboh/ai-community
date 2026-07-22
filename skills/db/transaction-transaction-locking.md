---
name: "트랜잭션과 락 (Transaction & Locking)"
description: "트랜잭션 경계·전파·격리수준·비관/낙관 락·데드락 회피의 범용 표준이다: 트랜잭션은 짧게, 외부 I/O는 경계 밖으로, 충돌 빈도로 락 전략을 고르고 락 순서를 일관되게 한다. 트랜잭션 경계를 정하거나 동시 갱신·재고 차감·이체 정합성을 다룰 때, 커넥션 풀 고갈·데드락·롤백 누락을 점검할 때 읽는다. 키워드: transaction, propagation, isolation, deadlock, pessimistic lock, optimistic lock, SELECT FOR UPDATE, version, rollback."
---

# 트랜잭션과 락 (Transaction & Locking)

**ID:** `SKL-TRANSACTION-LOCKING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 트랜잭션 경계·전파·격리수준·비관/낙관 락·데드락 회피의 범용 표준이다: 트랜잭션은 짧게, 외부 I/O는 경계 밖으로, 충돌 빈도로 락 전략을 고르고 락 순서를 일관되게 한다. 트랜잭션 경계를 정하거나 동시 갱신·재고 차감·이체 정합성을 다룰 때, 커넥션 풀 고갈·데드락·롤백 누락을 점검할 때 읽는다. 키워드: transaction, propagation, isolation, deadlock, pessimistic lock, optimistic lock, SELECT FOR UPDATE, version, rollback.

---

## 지시사항 (Instructions)

1. 트랜잭션 경계는 비즈니스 계층(서비스 단위)에: 한 단위 작업의 시작과 끝을 서비스 경계에 둔다. 진입점(컨트롤러/핸들러)이나 데이터 접근 계층에 트랜잭션 경계를 두지 않는다: 책임 경계가 무너진다.
2. 트랜잭션은 짧게 유지한다: 트랜잭션 길이 = 커넥션 점유 시간 = 동시성 한계. 모든 최적화는 '트랜잭션을 짧게'로 귀결된다.
3. 트랜잭션 안에서 외부 호출·대기를 금지한다: 외부 API 호출, 슬립/대기, 대용량 I/O를 트랜잭션 경계 안에서 하지 않는다: 그동안 커넥션을 붙잡아 풀을 고갈시킨다 (connection-pool-tuning 스킬의 풀 고갈 시나리오와 직결).
4. 충돌 빈도로 락 전략을 고른다: 동시 수정 충돌이 드물면 버전 기반 낙관 락으로 감지·재시도하고, 충돌이 잦거나 정합성이 결정적이면 행 잠금 기반 비관 락을 쓴다.
5. 여러 자원은 항상 같은 순서로 잠근다: 락 획득 순서를 일관되게 정해 데드락을 회피한다.
6. 격리 수준은 필요한 만큼만 올린다: 기본은 커밋된 읽기, 동일성 보장이 필요한 구간만 한 단계 올리고, 직렬화는 최후 수단으로 둔다.
7. 실패는 반드시 롤백으로 이어지게 한다: 예외를 삼키거나, 롤백 대상에서 빠지는 예외 유형을 방치해 부분 커밋이 새지 않게 한다.

## 태그

`transaction` `propagation` `isolation` `deadlock` `pessimistic lock` `optimistic lock` `SELECT FOR UPDATE` `version` `rollback` `@Transactional` `ACID` `PESSIMISTIC` `OPTIMISTIC` `rollbackFor` `transaction-locking` `db` `ai-recommended`
