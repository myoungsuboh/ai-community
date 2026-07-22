---
name: "스케줄링 및 비동기 처리 표준 (@Scheduled + @Async + ShedLock)"
description: "Spring Boot 주기 작업 스케줄링과 비동기 실행 표준: 다중 인스턴스에서 ShedLock으로 중복 실행 방지, 전용 ThreadPoolTaskExecutor 등록, @Async 자기호출 함정, 비동기-트랜잭션 분리, Graceful Shutdown. 스케줄러나 비동기 메서드를 만들거나 중복 실행·풀 격리·트랜잭션 문제를 다룰 때 읽는다. 키워드: @Scheduled, @Async, TaskExecutor, ThreadPoolTaskExecutor, ShedLock, cron, fixedRate, fixedDelay."
---

# 스케줄링 및 비동기 처리 표준 (@Scheduled + @Async + ShedLock)

**ID:** `SKL-SCHEDULER-ASYNC`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** Spring Boot 주기 작업 스케줄링과 비동기 실행 표준: 다중 인스턴스에서 ShedLock으로 중복 실행 방지, 전용 ThreadPoolTaskExecutor 등록, @Async 자기호출 함정, 비동기-트랜잭션 분리, Graceful Shutdown. 스케줄러나 비동기 메서드를 만들거나 중복 실행·풀 격리·트랜잭션 문제를 다룰 때 읽는다. 키워드: @Scheduled, @Async, TaskExecutor, ThreadPoolTaskExecutor, ShedLock, cron, fixedRate, fixedDelay.

---

## 지시사항 (Instructions)

1. 다중 인스턴스 환경의 @Scheduled 작업은 ShedLock으로 중복 실행을 막는다.
2. @Async 작업은 전용 ThreadPoolTaskExecutor 빈을 등록해 격리한다: 용도별로 분리한다.
3. @Async 자기호출은 프록시를 우회하므로 별도 빈으로 분리해 호출한다.
4. 비동기 메서드는 호출자의 트랜잭션과 분리해 새 트랜잭션으로 실행한다.
5. 스케줄 주기와 스레드풀 크기는 application.yml로 환경별 설정한다.

## 태그

`@Scheduled` `@Async` `TaskExecutor` `ThreadPoolTaskExecutor` `ShedLock` `cron` `fixedRate` `fixedDelay` `scheduler-async` `backEnd` `ai-recommended`
