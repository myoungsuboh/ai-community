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
- [완료] Phase 1: Foundation & Core Setup (commit bd8e817)
  - BE 멀티모듈 빌드 성공, auth-service DB연결+Flyway V1 적용 확인(12 테이블), FE 2종 빌드+린트 통과, 렌더링 확인.
- [완료] Phase 2: 인증 & 사용자 컨텍스트
  - common: spring-security + JWT(access/refresh) + JwtAuthenticationFilter + SecurityConfig(permitAll+메서드보안) + RefreshTokenStore(인메모리, 회전/재사용탐지/패밀리무효화) + BCrypt + Clock + DateTimeProvider(OffsetDateTime) + CORS(credentials).
  - auth-service: User 엔티티/Repo, 회원가입/로그인/refresh/logout, 5회실패 10분잠금(POL-37, noRollbackFor 로 실패카운트 커밋), 세션7일(POL-40), 비번8자(POL-39). 도메인이벤트 3종 발행.
  - FE mobile-web: 로그인/회원가입 실 API 연동. access토큰=메모리, refresh=HttpOnly 쿠키, 로드 시 silent refresh. (routing-auth/jwt-rotation 규칙 준수)
  - 검증: auth 단위+통합 테스트 9개 통과. 브라우저 로그인 플로우 실동작 확인(login 200, /home 리다이렉트, 닉네임 표시, HttpOnly 쿠키 JS 비노출, CORS preflight 200).
  - 미결/다음 phase: admin-web 인증연동은 Phase 3(검수함)에서. POL-38(전체 데이터 at-rest 암호화)은 인프라/Phase 8. RefreshTokenStore Redis 구현은 Phase 8.
  - 테스트 계정: uiuser@ai.community / uipass1234 (MEMBER). (member1@ai.community/testpass123 는 잠금테스트로 일시 잠김→10분 후 해제)
- [완료] Phase 3: 큐레이션 & 콘텐츠
  - curation-service: Post/Submission/Card(쓰기)/AuditLog. API: POST/GET /posts, GET /posts/{id}(추가), POST /submissions, GET /submissions?status(추가·검수함 읽기측), PATCH /submissions/{id}/review, PATCH /cards/{id}. 정책 POL-01/03/05/06/08/09/10/13/35/36.
  - content-service: Card(읽기). API: GET /cards(필터 category/minScore/sort + 페이지네이션), GET /cards/{slug}. 정책 POL-13/14/16/27.
  - 서비스 간 공유: cards 테이블을 curation(쓰기)·content(읽기)가 공유. 이벤트 5종 발행.
  - 서비스 매핑은 3_architecture.md §5 를 그대로 따름(스펙상 posts→curation, cards→content 로 라벨이 교차돼 보이지만 권위 문서 기준).
  - **명세에 없어 추가한 읽기 엔드포인트 2개**: GET /submissions(검수함 목록), GET /posts/{id}(게시글 상세). 지정된 화면 구현에 필수 → STOP 보고에 명시.
  - FE mobile: 홈피드(카드그리드+필터, 실전점수 도넛), 카드상세(4축), 게시글 목록/상세/작성, 제보. TanStack Vue Query(로딩/에러/빈상태/무효화).
  - FE admin: 큐레이터 로그인(역할 검증) + 검수함(목록 + 발행/반려 다이얼로그). 쿠키 인증 동기화.
  - **가드 타이밍 버그 수정**: 라우터 가드가 세션 복원(silent refresh) 완료를 await 하도록(ensureReady 메모이즈) → 새로고침 시 보호 라우트 오판 방지. mobile/admin 공통.
  - 검증: BE 단위+통합 32개 통과(auth9/curation16/content7). curl 전 파이프라인(제보→검수→발행→피드→상세, 게시글, 정책 401/403/404/400/422) 확인. 브라우저: 홈피드·카드상세·검수함이 실 데이터 렌더 + 큐레이터 인증 확인.
  - 다음: 반응/댓글(좋아요·북마크·댓글)은 Phase 4. POL-38 at-rest 암호화·POL-34 OWASP 는 Phase 8.
- [완료] Phase 4: 사용자 활동
  - user-activity-service: Reaction(좋아요/북마크 토글), Comment(작성/목록/신고/숨김해제), CommentReport. 이벤트 5종.
  - ⚠️ 해소: 댓글 신고 요청 스펙 → **사용자 결정: 사유 없이 신고만** (본문 없음). 설계 이벤트 페이로드와 일치.
  - 정책: POL-02(분당3건 429), POL-04(uq 1회), POL-07(500자), POL-15(24h 미만 계정 카운터 미반영 — AccountAgeChecker), POL-18(신고3건 자동숨김), POL-20/22/24/25(FE).
  - soft-delete: comments.deleted_at (V2), 활성 필터. 반응 토글오프는 물리삭제(토글 성격).
  - 공유 DB 상호작용: user-activity 가 cards 카운터 갱신(CardCounterUpdater, JdbcTemplate)·users.created_at 읽기(AccountAgeChecker). 문서화된 결정.
  - 명세 외 추가(화면 필수): GET /cards/{id}/comments (카드 상세 댓글 목록).
  - FE: 내 서재(무한스크롤 IntersectionObserver, POL-22/24), 카드상세 좋아요/북마크/댓글/신고 연동(POL-20/25 로그인 유도).
  - 검증: 단위+통합 13개 통과(총 45개). curl 전체 흐름(토글·서재·403·댓글·POL-02 429·신고·숨김·비큐레이터 403). 브라우저: 내 서재 실 북마크 렌더 확인.
  - 참고: 현재 모든 계정이 가입 24h 미만이라 POL-15 로 카드 카운터는 0 유지(정상 동작). 카운터 증가 로직은 단위테스트로 검증됨.
- [완료] Phase 5: Q&A
  - qna-service: Question/Answer. POST /questions, POST /questions/{id}/answers. 이벤트 2종(QuestionCreated/AnswerCreated). POL-11(@NotBlank), POL-28(FK CASCADE).
  - 명세 외 추가(화면 필수): GET /questions(목록), GET /questions/{id}(상세+답변).
  - FE: Q&A 목록(/qna) — 질문 등록 다이얼로그 + 아코디언 패널(펼치면 답변 조회 + 답변 작성). 실 API 연동.
  - **공통 수정(전 서비스 영향)**: BaseTimeEntity 가 Persistable 구현 → UUID 수동할당 엔티티가 merge 대신 persist 로 저장. 생성 응답의 created_at/updated_at 이 즉시 채워지고 불필요한 SELECT 제거. 전체 재빌드+테스트로 회귀 없음 확인.
  - 검증: 단위+통합 8개 통과(총 53). curl 질문·답변·목록·상세·POL-11 400·비인증 401. 브라우저 Q&A 목록 실데이터 렌더 + createdAt 채워짐 확인.
- [대기] Phase 6: 프로젝트 — 사용자 확인 후 시작.
