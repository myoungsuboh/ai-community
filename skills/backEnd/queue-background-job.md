---
name: "백그라운드 잡 & 큐 (스택 중립)"
description: "비동기 작업 큐 설계, 재시도 전략, 데드레터 큐, 모니터링 표준 (스택 중립). 긴 작업을 HTTP 요청에서 분리해 큐로 처리하거나, 재시도·멱등성·DLQ를 설계할 때 읽는다. 키워드: queue, job, background, celery, bull, sidekiq, dead-letter, retry, async."
---

# 백그라운드 잡 & 큐 (스택 중립)

**ID:** `SKL-BACKGROUND-JOB`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 비동기 작업 큐 설계, 재시도 전략, 데드레터 큐, 모니터링 표준 (스택 중립). 긴 작업을 HTTP 요청에서 분리해 큐로 처리하거나, 재시도·멱등성·DLQ를 설계할 때 읽는다. 키워드: queue, job, background, celery, bull, sidekiq, dead-letter, retry, async.

---

## 지시사항 (Instructions)

1. 긴 작업(이메일·리포트·외부 API 호출)은 HTTP 요청에서 분리해 큐에 넣고 즉시 202 Accepted를 반환한다.
2. 실패한 잡은 지수 백오프(예: 1m→5m→15m→1h)로 최대 N회 재시도하고, 초과 시 Dead Letter Queue로 이동한다.
3. 잡 정의는 멱등하게 설계한다: 같은 잡이 두 번 실행돼도 부작용이 중복되지 않도록 한다.
4. 잡별 timeout을 설정하고, 실행 시간이 길어지면 heartbeat로 활성 상태를 갱신한다.
5. DLQ를 주기적으로 모니터링하고, 수동 재처리 인터페이스를 제공한다.

## 태그

`queue` `job` `background` `celery` `bull` `sidekiq` `dead-letter` `retry` `async` `background-job` `backEnd` `ai-recommended`
