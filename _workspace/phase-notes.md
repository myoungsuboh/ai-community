# _workspace / Phase Notes — AI 커뮤니티

> 매 Phase 시작 시 이 파일 + 관련 spec 을 다시 읽는다 (기억에 의존 금지).

## 확정된 핵심 결정 (Architectural Decisions)

1. **Tech stack (3_architecture.md 권위)**: 백엔드 = Spring Boot(Java 17), 프론트 = Vue 3 + Vuetify 3, DB = PostgreSQL, 캐시/세션 = Redis.
2. **실행 환경 (사용자 승인 2026-07-22)**: 이 PC 에 Docker 미설치 → `local` 프로필로 실행.
   - `local` (기본): H2 파일 DB (PostgreSQL 호환 모드) + 인메모리 캐시/레이트리밋(Redis 대체 구현).
   - `docker`: 설계서 원본대로 PostgreSQL + Redis (`docker-compose.dev.yml`). 나중에 Docker 켜면 전환 가능.
   - 캐시/세션/레이트리밋은 인터페이스로 추상화 → 프로필별 구현 교체 (Redis impl vs InMemory impl).
3. **DB 구성**: 설계서 Connection Map 대로 **단일 주 데이터베이스**를 모든 서비스가 공유. Flyway 마이그레이션은 `common` 모듈에 모아두고 모든 서비스가 공유 DB 에 적용(Flyway 락으로 경합 방지). H2 는 `AUTO_SERVER=TRUE` 로 다중 JVM 동시 접속 허용.
4. **서비스 구조**: `BE/` Gradle 멀티모듈. 각 서비스는 독립 실행 가능한 Spring Boot 모듈(고유 포트). `common` 공용 모듈 공유.
   - auth-service:8081, content-service:8082, curation-service:8083, user-activity-service:8084, qna-service:8085, project-service:8086, ranking-api-service:8087, ranking-batch-worker(스케줄러, 웹 없음).
   - 서비스 간 참조는 FK 가 아니라 ID 로 (authorId 등). 마이크로서비스 원칙.
5. **프론트**: `FE/mobile-web`(사용자, 포트 5173), `FE/admin-web`(관리자/큐레이터, 포트 5174). Vite dev proxy 로 각 백엔드 포트에 연결. 실제 API 연동(목데이터 금지).
6. **디자인 요구(사용자)**: FE 첫인상 중요 → "large typography" 감성(refero.design 참고). Vuetify 위에 큰 타이포/여백 중심 커스텀 테마 적용.

## 스펙 갭 / 확인 필요
- ⚠️ `POST /api/v1/comments/{commentId}/report` 요청 본문 스펙 미정 (IMPLEMENTATION-CHECKLIST). → **Phase 4 STOP 에서 사용자 확인 후 구현.** 잠정: 본문 없음 또는 `{ "reason"?: string }`.
- `1_spack.md` 파일 손상(내용 4회 반복 + POST/GET /posts 만 상세). 나머지 API 상세 스키마는 3_architecture.md 의 API↔Service 매핑 + 2_ddd.md 이벤트 페이로드 + 응답 본문 규약으로 도출.

## 진행 상황
- [진행중] Phase 1: Foundation & Core Setup
