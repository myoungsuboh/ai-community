---
name: "Redis 캐싱 전략"
description: "Redis 캐시 설계 표준: Key 네이밍, TTL 관리, Cache-Aside 패턴, 실시간 센서 데이터 Hash 서빙, AI 이상 점수 서빙, 로컬 Redis 미실행 시 연결 실패 처리. 캐시를 새로 붙이거나 키·TTL을 정할 때, Redis 연결 오류를 다룰 때 읽는다. 키워드: redis, RedisTemplate, @Cacheable, @CacheEvict, Lettuce, spring-data-redis, RedisConnectionFailureException, Cache-Aside."
---

# Redis 캐싱 전략

**ID:** `SKL-REDIS-CACHE`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** Redis 캐시 설계 표준: Key 네이밍, TTL 관리, Cache-Aside 패턴, 실시간 센서 데이터 Hash 서빙, AI 이상 점수 서빙, 로컬 Redis 미실행 시 연결 실패 처리. 캐시를 새로 붙이거나 키·TTL을 정할 때, Redis 연결 오류를 다룰 때 읽는다. 키워드: redis, RedisTemplate, @Cacheable, @CacheEvict, Lettuce, spring-data-redis, RedisConnectionFailureException, Cache-Aside.

---

## 지시사항 (Instructions)

1. 캐시 키는 {서비스}:{도메인}:{식별자} 형식으로 짓고 콜론(:)으로 네임스페이스를 구분한다.
2. 데이터 성격별로 TTL을 다르게 설정한다: 실시간 센서는 짧게, 정적 데이터는 길게. TTL 없으면 메모리가 계속 쌓인다.
3. Cache-Aside 패턴으로 캐시 미스 시 DB 조회 후 캐시에 채운다.
4. Redis 연결 실패 시 예외를 잡아 DB fallback으로 서비스를 유지한다.
5. 실시간 센서 데이터는 Hash 구조로 저장해 필드 단위로 갱신한다.

## 태그

`redis` `RedisTemplate` `@Cacheable` `@CacheEvict` `Lettuce` `spring-data-redis` `RedisConnectionFailureException` `Cache-Aside` `redis-cache` `backEnd` `ai-recommended`
