---
name: "Rate Limiting (Bucket4j + Redis)"
description: "Spring Boot에서 IP/사용자 기반 API throttling을 분산 환경에서 일관되게 구현하는 표준. Bucket4j + Redis token bucket, 엔드포인트별 정책, 429 + Retry-After 응답을 다룬다. 공개 API를 출시하거나 로그인 무차별 대입·봇·스팸을 막을 때 읽는다. 키워드: bucket4j, RateLimit, Bucket, Refill, Bandwidth, tooManyRequests, 429, throttling."
---

# Rate Limiting (Bucket4j + Redis)

**ID:** `SKL-RATE-LIMITING-BUCKET4J`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** Spring Boot에서 IP/사용자 기반 API throttling을 분산 환경에서 일관되게 구현하는 표준. Bucket4j + Redis token bucket, 엔드포인트별 정책, 429 + Retry-After 응답을 다룬다. 공개 API를 출시하거나 로그인 무차별 대입·봇·스팸을 막을 때 읽는다. 키워드: bucket4j, RateLimit, Bucket, Refill, Bandwidth, tooManyRequests, 429, throttling.

---

## 지시사항 (Instructions)

1. API throttling은 Bucket4j 토큰 버킷 알고리즘으로 구현한다.
2. 분산 환경에서는 Redis를 버킷 저장소로 사용해 인스턴스 간 한도를 공유한다(in-memory Map 금지).
3. 한도 정책은 엔드포인트별로 정의하고 어노테이션으로 적용한다(enum 중앙 관리).
4. 한도 초과 시 429 상태와 Retry-After 헤더로 재시도 시각을 알린다.
5. 클라이언트 IP는 X-Forwarded-For의 첫 값을 신뢰 가능한 프록시 기준으로 추출한다.

## 태그

`bucket4j` `RateLimit` `Bucket` `Refill` `Bandwidth` `tooManyRequests` `429` `throttling` `rate-limiting-bucket4j` `backEnd` `ai-recommended`
