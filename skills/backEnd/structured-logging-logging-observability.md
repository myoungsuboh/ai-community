---
name: "로깅 및 옵저버빌리티 표준 (Logging & Observability)"
description: "구조화 로깅(JSON), 상관관계 ID(traceId) 전파, 민감정보 마스킹, 로그 레벨 정책, 메트릭/RED, W3C 분산 추적의 스택 무관 범용 표준. 로그를 작성·설정하거나, 운영에서 문제를 빠르게 추적할 환경을 갖출 때, 새 서비스/언어/프레임워크에 로깅·메트릭을 도입할 때 읽는다. 키워드: structured logging, JSON log, correlation id, traceId, spanId, MDC, masking, PII, log level, metrics, RED, Prometheus, OpenTelemetry, W3C traceparent."
---

# 로깅 및 옵저버빌리티 표준 (Logging & Observability)

**ID:** `SKL-LOGGING-OBSERVABILITY`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 구조화 로깅(JSON), 상관관계 ID(traceId) 전파, 민감정보 마스킹, 로그 레벨 정책, 메트릭/RED, W3C 분산 추적의 스택 무관 범용 표준. 로그를 작성·설정하거나, 운영에서 문제를 빠르게 추적할 환경을 갖출 때, 새 서비스/언어/프레임워크에 로깅·메트릭을 도입할 때 읽는다. 키워드: structured logging, JSON log, correlation id, traceId, spanId, MDC, masking, PII, log level, metrics, RED, Prometheus, OpenTelemetry, W3C traceparent.

---

## 지시사항 (Instructions)

1. 구조화 로깅: 로그는 사람이 읽는 자유 텍스트가 아니라 기계가 파싱 가능한 키-값(JSON 등) 구조로 출력한다. 메시지는 고정, 가변값은 별도 필드로 분리한다.
2. 상관관계 ID 전파: 하나의 요청을 끝까지 추적할 수 있도록 모든 로그에 상관관계 ID(traceId)와 주체 식별자(userId 등)를 자동으로 실어 보낸다. 서비스 경계를 넘어도 같은 ID가 유지된다.
3. 민감정보 마스킹: 비밀번호·토큰·주민번호·카드번호 같은 민감정보는 로그에 남기기 전에 마스킹하거나 아예 기록하지 않는다.
4. 로그 레벨 정책: ERROR/WARN/INFO/DEBUG/TRACE의 의미를 팀 전체가 동일하게 쓰고, 환경(로컬/운영)에 따라 출력 레벨과 포맷만 분리한다.
5. 메트릭(RED): 로그와 별개로 수치 지표를 노출한다. 최소한 요청 수(Rate)·오류 수(Errors)·지연(Duration)을 측정해 추세와 이상을 감지한다.
6. 표준 추적 전파: 분산 추적은 W3C traceparent/tracestate 같은 표준 헤더로 전파해 벤더·언어가 달라도 추적이 이어지게 한다.

## 태그

`structured logging` `JSON log` `correlation id` `traceId` `spanId` `MDC` `masking` `PII` `log level` `metrics` `RED` `Prometheus` `OpenTelemetry` `W3C traceparent` `slf4j` `logback` `Logger` `LoggerFactory` `@Slf4j` `log.info` `log.warn` `logging-observability` `backEnd` `ai-recommended`
