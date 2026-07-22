# 00-ORCHESTRATOR: Vibe Coding Master Plan

> **Agent Instructions:**
> You are ONE coding agent executing this plan sequentially. **DO NOT SKIP PHASES.**
> **Recommended runtime:** this package is designed to complete reliably on Claude Sonnet / GPT-4-class (or stronger) agents; lighter models will need noticeably more STOP interventions.
> For each task, if a `Target Skill` is assigned (not `none`), read that file from the `skills/` directory before writing code.
> Write durable intermediate artifacts to `_workspace/` and re-read them (and the relevant spec file) at the start of each phase — do not trust memory.
> After each phase: run its Verify step, loop fixes until green (max 3), then STOP and wait for the user's confirmation.
> **Never push during the build:** no `git push` or any remote upload until the Final Phase's SHIP_TO_HARNESS step — commits stay local until then.
> **Database writes (only if the plan has a database):** before EVERY migration/seed run the target must be local (`localhost`/`127.0.0.1`/a service from this project's own docker-compose) — anything else: STOP and ask the user first. Seed idempotently per entity (skip a table/collection that already has rows — NOT the whole database, or later phases would never seed).
> **Screens (only if the design has frontend screens):** every screen ships wired to the real BE API — no mock or hardcoded data left on the primary path. The Integration phase's Verify must drive one primary user flow through the real FE against the running BE (FE → BE → DB when one exists) and record the commands + observed results in its STOP report.
> **Resuming after an interruption?** Read `IMPLEMENTATION-CHECKLIST.md` and `_workspace/` first to see what is already done, then continue from the first unchecked item — do not start over.

## Project Size: Large
**Rationale:** 7개의 바운디드 컨텍스트, 9개의 애그리게이트, 10개의 서비스 (8개 백엔드, 2개 프론트엔드), 21개의 API 및 17개의 화면은 복잡한 시스템을 나타내며, 구조화된 컨텍스트 중심 접근 방식이 필요합니다.

## Phase 1: Foundation & Core Setup
- [ ] Task 1.1: 프로젝트 구조 초기화. 루트 디렉토리를 `FE/`와 `BE/`로 분할하고, `git init -b main`으로 Git 저장소를 초기화하며, `.gitignore` 파일을 생성합니다 (예: `node_modules/`, 빌드 아티팩트, `.env` 파일 제외). `FE/mobile-web`, `FE/admin-web` 디렉토리와 각 백엔드 서비스 (`BE/auth-service`, `BE/user-activity-service` 등) 디렉토리를 생성합니다. (Target Skill: `skills/core/git-git-workflow.md`)
- [ ] Task 1.2: DDD 및 Spack 명세에 따라 도메인 모델(엔티티)을 정의하고, 초기 DB 스키마를 `_workspace/db_schema.json`에 저장합니다. (Target Skill: `skills/db/er-er-modeling.md`)
- [ ] Task 1.3: 데이터베이스 설정. 기존 데이터베이스 구성(docker-compose, `.env`, 프레임워크 설정, 마이그레이션 폴더 등)을 먼저 확인합니다. 발견되지 않으면, Architecture 명세에 따라 PostgreSQL 및 Redis를 포함하는 `docker-compose.dev.yml` 파일을 생성하고 `docker compose up -d`로 실행합니다. `.env.example` 파일을 생성하여 DB 연결 설정을 포함합니다. Flyway를 사용하여 초기 스키마 마이그레이션을 적용합니다. (Target Skill: `skills/devops/container-docker-containerization.md`)
- [ ] Task 1.4: 백엔드 서비스 기본 설정. 각 Spring Boot 서비스 (`BE/auth-service`, `BE/curation-service` 등)의 기본 프로젝트 구조를 설정하고, 공통 의존성 및 설정(예: 부모 POM, 공통 유틸리티 모듈)을 구성합니다. (Target Skill: `skills/backEnd/MVC-architecture-layering.md`)
- [ ] Task 1.5: 프론트엔드 애플리케이션 기본 설정. 각 Vue.js 애플리케이션 (`FE/mobile-web`, `FE/admin-web`)의 기본 프로젝트 구조를 설정하고, 공통 의존성, 라우팅 및 Vuetify UI 프레임워크를 설정합니다. (Target Skill: `skills/frontEnd/vuetify-ui-vuetify.md`)
- [ ] Verify 1: 백엔드 및 프론트엔드 프로젝트가 성공적으로 빌드되고 린트 검사를 통과하는지 확인합니다. 데이터베이스 연결이 성공하고 Flyway 마이그레이션이 적용되었는지 확인합니다. `docker compose ps` 및 `curl` 명령을 사용하여 DB 연결을 테스트합니다. → fix-loop until green (max 3) → adversarial self-review (or subagent review) → commit (`git add -A && git commit`) → **STOP for user confirmation**

## Phase 2: 인증 & 사용자 컨텍스트 구현
- [ ] Task 2.1: `User` 엔티티/애그리게이트를 구현하고, `BE/auth-service`에 `로그인`, `회원가입` API 및 관련 비즈니스 로직을 구현합니다. JWT Refresh Token 회전 및 재사용 탐지 기능을 포함합니다. 사용자 개인정보 보호 및 비밀번호 해싱을 적용합니다. (Target Skill: `skills/security/jwt-jwt-refresh-rotation.md`)
- [ ] Task 2.2: `FE/mobile-web`에 `로그인/회원가입` 화면을 구현하고, `BE/auth-service`의 실제 API와 연동합니다. 폼 검증 및 라우팅 인증/권한 처리를 적용합니다. (Target Skill: `skills/frontEnd/routing-routing-auth.md`)
- [ ] Task 2.3: `BE/auth-service`에 대한 단위 및 통합 테스트를 작성하고 실행합니다. (Target Skill: `skills/backEnd/testing-junit-mockito.md`)
- [ ] Verify 2: `BE/auth-service` 및 `FE/mobile-web`가 성공적으로 빌드되고 린트 검사를 통과하는지 확인합니다. `BE/auth-service`의 API 테스트가 통과하는지 확인합니다. `FE/mobile-web`에서 로그인/회원가입 플로우가 실제 백엔드와 연동되어 정상 작동하는지 확인합니다. 테스트 계정 정보(예: `user: testuser`, `password: testpass`)를 STOP 보고서에 명시합니다. (Target Skill: `skills/core/verification-verification-before-comple.md`) → fix-loop (max 3) → adversarial self-review → commit → **STOP**

## Phase 3: 큐레이션 & 콘텐츠 컨텍스트 구현
- [ ] Task 3.1: `Post`, `Card`, `Submission` 엔티티/애그리게이트를 구현하고, `BE/curation-service` 및 `BE/content-service`에 관련 API (`게시글 작성/검색/필터링`, `카드 상세/목록/필터링`, `URL 제보`, `제보 검수/발행/반려`, `발행 카드 수정`) 및 비즈니스 로직을 구현합니다. 페이지네이션 및 필터링 표준을 적용합니다. (Target Skill: `skills/backEnd/pagination-pagination-filtering.md`)
- [ ] Task 3.2: `FE/mobile-web`에 `게시글 목록`, `게시글 상세`, `홈 피드`, `카드 상세`, `제보하기` 화면을 구현하고, `BE/curation-service` 및 `BE/content-service`의 실제 API와 연동합니다. `FE/admin-web`에 `큐레이터 검수함` 화면을 구현하고 `BE/curation-service`와 연동합니다. (Target Skill: `skills/frontEnd/server-state-server-state.md`)
- [ ] Task 3.3: `BE/curation-service` 및 `BE/content-service`에 대한 단위 및 통합 테스트를 작성하고 실행합니다. (Target Skill: `skills/testing/testcontainers-integration-testing.md`)
- [ ] Verify 3: `BE/curation-service`, `BE/content-service`, `FE/mobile-web`, `FE/admin-web`가 성공적으로 빌드되고 린트 검사를 통과하는지 확인합니다. 관련 API 테스트가 통과하는지 확인합니다. 프론트엔드 화면이 실제 백엔드에서 데이터를 가져와 정상적으로 표시되는지 확인합니다. (Target Skill: `skills/core/verification-verification-before-comple.md`) → fix-loop (max 3) → adversarial self-review → commit → **STOP**

## Phase 4: 사용자 활동 컨텍스트 구현
- [ ] Task 4.1: `Reaction`, `Comment` 엔티티/애그리게이트를 구현하고, `BE/user-activity-service`에 관련 API (`좋아요/북마크 토글`, `내 서재 조회`, `댓글 작성/신고/숨김/해제`) 및 비즈니스 로직을 구현합니다. Soft Delete 및 감사 컬럼을 적용합니다. (Target Skill: `skills/db/soft-delete-soft-delete-audit.md`)
- [ ] Task 4.2: `FE/mobile-web`에 `내 서재` 화면을 구현하고, `BE/user-activity-service`의 실제 API와 연동합니다. (Target Skill: `skills/frontEnd/virtual-scroll-virtual-scroll.md`)
- [ ] Task 4.3: `BE/user-activity-service`에 대한 단위 및 통합 테스트를 작성하고 실행합니다. (Target Skill: `skills/testing/test-test-strategy.md`)
- [ ] Verify 4: `BE/user-activity-service` 및 `FE/mobile-web`가 성공적으로 빌드되고 린트 검사를 통과하는지 확인합니다. 관련 API 테스트가 통과하는지 확인합니다. `내 서재` 화면이 실제 백엔드에서 데이터를 가져와 정상적으로 표시되는지 확인합니다. (Target Skill: `skills/core/verification-verification-before-comple.md`) → fix-loop (max 3) → adversarial self-review → commit → **STOP**

## Phase 5: Q&A 컨텍스트 구현
- [ ] Task 5.1: `Question`, `Answer` 엔티티/애그리게이트를 구현하고, `BE/qna-service`에 관련 API (`질문 등록`, `답변 등록`) 및 비즈니스 로직을 구현합니다. (Target Skill: `skills/backEnd/RestController-spring-boot-rest.md`)
- [ ] Task 5.2: `FE/mobile-web`에 `Q&A 목록` 화면을 구현하고, `BE/qna-service`의 실제 API와 연동합니다. (Target Skill: none)
- [ ] Task 5.3: `BE/qna-service`에 대한 단위 및 통합 테스트를 작성하고 실행합니다. (Target Skill: `skills/testing/jest-unit-testing.md`)
- [ ] Verify 5: `BE/qna-service` 및 `FE/mobile-web`가 성공적으로 빌드되고 린트 검사를 통과하는지 확인합니다. 관련 API 테스트가 통과하는지 확인합니다. `Q&A 목록` 화면이 실제 백엔드에서 데이터를 가져와 정상적으로 표시되는지 확인합니다. (Target Skill: `skills/core/verification-verification-before-comple.md`) → fix-loop (max 3) → adversarial self-review → commit → **STOP**

## Phase 6: 프로젝트 컨텍스트 구현
- [ ] Task 6.1: `Project` 엔티티/애그리게이트를 구현하고, `BE/project-service`에 관련 API (`프로젝트 생성/수정/진행 상황 업데이트`) 및 비즈니스 로직을 구현합니다. (Target Skill: `skills/backEnd/validation-validation-bean.md`)
- [ ] Task 6.2: `FE/mobile-web`에 `프로젝트 목록`, `프로젝트 생성`, `프로젝트 상세` 화면을 구현하고, `BE/project-service`의 실제 API와 연동합니다. (Target Skill: none)
- [ ] Task 6.3: `BE/project-service`에 대한 단위 및 통합 테스트를 작성하고 실행합니다. (Target Skill: `skills/backEnd/testing-junit-mockito.md`)
- [ ] Verify 6: `BE/project-service` 및 `FE/mobile-web`가 성공적으로 빌드되고 린트 검사를 통과하는지 확인합니다. 관련 API 테스트가 통과하는지 확인합니다. 프로젝트 관련 화면이 실제 백엔드에서 데이터를 가져와 정상적으로 표시되는지 확인합니다. (Target Skill: `skills/core/verification-verification-before-comple.md`) → fix-loop (max 3) → adversarial self-review → commit → **STOP**

## Phase 7: 랭킹 컨텍스트 구현
- [ ] Task 7.1: `RankingSnapshot` 엔티티/애그리게이트를 구현하고, `BE/ranking-api-service`에 `주간 랭킹 조회` API 및 비즈니스 로직을 구현합니다. `BE/ranking-batch-worker`에 랭킹 계산을 위한 스케줄링된 백그라운드 잡을 구현합니다. (Target Skill: none)
- [ ] Task 7.2: `FE/mobile-web`에 `주간 랭킹` 화면을 구현하고, `BE/ranking-api-service`의 실제 API와 연동합니다. (Target Skill: none)
- [ ] Task 7.3: `BE/ranking-api-service` 및 `BE/ranking-batch-worker`에 대한 단위 및 통합 테스트를 작성하고 실행합니다. (Target Skill: none)
- [ ] Verify 7: `BE/ranking-api-service`, `BE/ranking-batch-worker`, `FE/mobile-web`가 성공적으로 빌드되고 린트 검사를 통과하는지 확인합니다. `주간 랭킹 조회` API 테스트가 통과하는지 확인합니다. `주간 랭킹` 화면이 실제 백엔드에서 데이터를 가져와 정상적으로 표시되는지 확인합니다. 배치 워커가 실행되어 랭킹 스냅샷이 생성되는지 DB를 통해 확인합니다. (Target Skill: `skills/core/verification-verification-before-comple.md`) → fix-loop (max 3) → adversarial self-review → commit → **STOP**

## Phase 8: Cross-Cutting Concerns & Integration
- [ ] Task 8.1: `AuditLog` 엔티티를 구현하고, 모든 백엔드 서비스에 보안 감사 로깅 및 이벤트 추적 기능을 통합합니다. (Target Skill: `skills/security/audit_log-audit-logging.md`)
- [ ] Task 8.2: 모든 정책(POL-XX)을 검토하고, Rate Limiting, 입력 검증, OWASP Top 10 보안 가이드라인, 전송 보안(HTTPS) 등 나머지 보안 및 운영 정책을 구현합니다. (Target Skill: none)
- [ ] Task 8.3: 모든 백엔드 서비스에 구조화된 로깅 및 옵저버빌리티 표준을 적용하고, 전역 에러 처리 및 회복탄력성 패턴을 구현합니다. (Target Skill: none)
- [ ] Task 8.4: `FE/mobile-web` 및 `FE/admin-web`에 대한 E2E 테스트를 작성하고, 주요 사용자 흐름이 백엔드와 완전히 통합되어 작동하는지 확인합니다. (Target Skill: `skills/testing/playwright-e2e-testing.md`)
- [ ] Verify 8: 모든 서비스가 성공적으로 실행되고, 프론트엔드 애플리케이션이 백엔드와 완전히 연동되어 기능하는지 확인합니다. E2E 테스트가 통과하고, 감사 로그가 정상적으로 생성되며, 보안 정책이 적용되었는지 확인합니다. 하나의 주요 사용자 흐름(예: 게시글 작성 → 홈 피드에서 확인 → 좋아요 토글)을 FE를 통해 실행하고, 그 과정에서 BE와 DB가 올바르게 작동하는지 확인한 후, 실행 명령과 관찰된 결과를 STOP 보고서에 기록합니다. (Target Skill: `skills/core/verification-verification-before-comple.md`) → fix-loop (max 3) → adversarial self-review → commit → **STOP**

## Final Phase: Full-Coverage Audit Loop
- [ ] `IMPLEMENTATION-CHECKLIST.md` 파일을 엽니다. (만약 파일이 없다면, `1_spack.md`를 기반으로 자체 체크리스트를 생성합니다.) 모든 항목에 대해 실제 코드에서 구현되었는지 확인하고, `- [x]`로 표시한 다음 `←구현위치:` 뒤에 실제 파일 경로를 작성합니다.
- [ ] 실제 파일 경로가 없는 항목은 구현되지 않은 것으로 간주합니다. 해당 항목을 구현한 다음, 이 감사 프로세스를 처음부터 다시 실행합니다.
- [ ] **100%의 항목이 체크될 때까지 반복합니다.** 최종 감사 결과(예: `N/N ✅`, 여기서 N은 총 항목 수)와 체크된 목록으로만 완료를 보고합니다.
- [ ] 100% 완료 후: `SHIP_TO_HARNESS.md` 파일을 열고 그 절차를 **직접** 실행합니다. (Git 설정, GitHub 업로드 등 모든 명령은 에이전트가 실행하며, 사용자는 클릭 및 승인만 합니다.) (만약 해당 파일이 없다면, 사용자에게 이 코드를 GitHub 저장소에 직접 푸시하도록 안내합니다.) 그 다음, 사용자에게 Harness로 돌아가 **Code** 화면에서 업로드된 저장소를 로드하고, **Lint** 화면에서 **Lint verification**을 실행하도록 안내합니다. (저장소는 검증 중에 **Deliverables** 화면에 자동으로 등록되며, 점수는 명세와 구현의 일치도를 나타냅니다. 낮은 점수는 격차를 해소하기 위한 수정 가이드를 제공합니다.)