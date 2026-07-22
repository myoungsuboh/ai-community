# AI 커뮤니티 (AI Community)

AI 레포·스킬을 커뮤니티가 직접 큐레이션하고, 함께 배우고(Q&A), 프로젝트로 협업하며,
매주 랭킹으로 트렌드를 확인하는 AI 지식 공유 플랫폼입니다.

이 저장소는 설계 패키지(`00-ORCHESTRATOR.md` 등)를 기반으로 구현됩니다.

## 구성 (아키텍처)

- **프론트엔드 (Vue 3 + Vuetify 3)**
  - `FE/mobile-web` — 사용자용 (포트 5173)
  - `FE/admin-web` — 큐레이터/관리자용 (포트 5174)
- **백엔드 (Spring Boot, Java 17) — `BE/` Gradle 멀티모듈**
  - `auth-service` (8081) · `content-service` (8082) · `curation-service` (8083)
  - `user-activity-service` (8084) · `qna-service` (8085) · `project-service` (8086)
  - `ranking-api-service` (8087) · `ranking-batch-worker` (스케줄러)
  - `common` — 공용 라이브러리(엔티티 기반클래스, 에러 처리, DB 마이그레이션)
- **데이터**
  - 주 데이터베이스: PostgreSQL (설계 기준) · 로컬은 H2(PostgreSQL 호환 모드)
  - 캐시/세션: Redis (설계 기준) · 로컬은 인메모리 대체

## 실행 방법

### 사전 준비물
- **Java 17** (백엔드)
- **Node.js 20+** (프론트엔드)
- (선택) **Docker** — 진짜 PostgreSQL + Redis 로 돌리고 싶을 때만

### 1) 로컬 실행 — Docker 불필요 (기본)
설치가 필요 없는 내장 DB(H2)로 바로 실행됩니다.

**가장 쉬운 방법 (Windows, 백엔드 8개 한 번에):**
```powershell
powershell -ExecutionPolicy Bypass -File run-all.ps1
```
그다음 프론트엔드(사용자 웹)를 새 터미널에서:
```bash
cd FE/mobile-web
npm install
npm run dev
```
브라우저에서 http://localhost:5173 접속.

관리자 콘솔이 필요하면 또 다른 터미널에서 `cd FE/admin-web && npm install && npm run dev` → http://localhost:5174

**개별 실행 (예: 인증 서비스만):**
```bash
cd BE
./gradlew :auth-service:bootRun
```

> ⚠️ 로그인·회원가입 등 모든 기능은 **백엔드가 켜져 있어야** 동작합니다. 서버가 꺼져 있으면
> 화면은 뜨지만 "네트워크 연결을 확인해 주세요" 오류가 납니다.

### 로그인 / 회원가입
- **회원가입**: 로그인 화면(`/login`)의 **[회원가입] 탭**에서 이메일·닉네임·비밀번호(8자 이상)로 가입.
- **기본 제공 계정** (auth 서비스 시작 시 자동 생성):
  - 큐레이터: `curator@ai.community` / `curator1234`
  - 관리자: `admin@ai.community` / `admin1234`

### 2) 진짜 PostgreSQL + Redis 로 실행 (Docker 필요)
```bash
docker compose -f docker-compose.dev.yml up -d
# 이후 백엔드를 docker 프로필로 실행
cd BE
SPRING_PROFILES_ACTIVE=docker ./gradlew :auth-service:bootRun
```

## 빌드 / 검증
```bash
# 백엔드 전체 빌드
cd BE && ./gradlew build

# 프론트엔드 빌드 + 린트
cd FE/mobile-web && npm run build && npm run lint
cd FE/admin-web  && npm run build && npm run lint
```

## 진행 상황
구현은 `00-ORCHESTRATOR.md` 의 Phase 순서를 따릅니다.
전체 항목 체크리스트는 `IMPLEMENTATION-CHECKLIST.md` 를 참고하세요.

- [x] **Phase 1 — 기반 구축**: 프로젝트 구조, 8개 백엔드 서비스 + 2개 프론트엔드 스캐폴드,
      DB 스키마/마이그레이션(Flyway), 공용 모듈, 디자인 시스템(Large Typography).
- [ ] Phase 2 — 인증 & 사용자
- [ ] Phase 3 — 큐레이션 & 콘텐츠
- [ ] Phase 4 — 사용자 활동
- [ ] Phase 5 — Q&A
- [ ] Phase 6 — 프로젝트
- [ ] Phase 7 — 랭킹
- [ ] Phase 8 — 통합 & 크로스커팅
