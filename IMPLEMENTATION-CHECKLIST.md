# ✅ IMPLEMENTATION-CHECKLIST — AI 커뮤니티

> 이 목록은 설계 그래프에서 **기계적으로 생성**되었습니다 — 명세 요약 과정의 누락이 없습니다.
> 
> **사용법 (AI 에이전트 필독):** 구현이 끝났다고 판단되면 이 파일의 각 항목을 실제 코드와
> 대조해 `- [x]` 로 바꾸고, 항목 끝 `←구현위치:` 뒤에 **실제 파일 경로**를 적으세요.
> 경로를 적은 파일은 실제로 **열어서 확인**하세요 — 열 수 없는 경로는 거짓 체크입니다.
> 경로를 적을 수 없는 항목은 **미구현**입니다 — 구현한 뒤 다시 대조하세요.
> **모든 항목(129개)이 [x] 가 될 때까지 이 루프를 반복**한 뒤에만 완료를 보고하세요.
> 
> ⚠️ **스펙 갭 1건**: ⚠️ 표시 항목은 설계에 스키마(요청/응답/속성)·담당 서비스·정책 내용이 비어 있습니다.
> **추측으로 채우지 마세요** — 해당 Phase 의 STOP 보고에서 사용자에게 확인을 받은 뒤 구현하세요.

## APIs (20)
- [x] `POST /api/v1/posts` — 게시글 작성 [→ 큐레이션 서비스]  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/web/PostController.java
- [x] `GET /api/v1/posts` — 게시글 검색 및 필터링 [→ 큐레이션 서비스]  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/web/PostController.java
- [x] `GET /api/v1/cards/{cardSlug}` — 카드 상세 조회 [→ 콘텐츠 서비스]  ←구현위치: BE/content-service/src/main/java/com/aicommunity/content/web/CardController.java
- [x] `GET /api/v1/cards` — 홈 피드 카드 목록 조회 및 필터링 [→ 콘텐츠 서비스]  ←구현위치: BE/content-service/src/main/java/com/aicommunity/content/web/CardController.java
- [x] `POST /api/v1/questions` — 질문 등록 [→ Q&A 서비스]  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/web/QuestionController.java
- [x] `POST /api/v1/questions/{questionId}/answers` — 답변 등록 [→ Q&A 서비스]  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/web/QuestionController.java
- [ ] `POST /api/v1/projects` — 프로젝트 생성 [→ 프로젝트 서비스]  ←구현위치: 
- [ ] `PUT /api/v1/projects/{projectId}` — 프로젝트 수정 [→ 프로젝트 서비스]  ←구현위치: 
- [ ] `PATCH /api/v1/projects/{projectId}/status` — 프로젝트 진행 상황 업데이트 [→ 프로젝트 서비스]  ←구현위치: 
- [x] `POST /api/v1/submissions` — URL 제보 [→ 큐레이션 서비스]  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/web/SubmissionController.java
- [x] `PATCH /api/v1/submissions/{submissionId}/review` — 제보 검수 및 발행/반려 [→ 큐레이션 서비스]  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/web/SubmissionController.java
- [x] `PATCH /api/v1/cards/{cardId}` — 발행 카드 수정 [→ 큐레이션 서비스]  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/web/CardController.java
- [x] `POST /api/v1/cards/{cardId}/reactions` — 좋아요 및 북마크 토글 [→ 사용자 활동 서비스]  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/web/ReactionController.java
- [x] `GET /api/v1/users/{userId}/bookmarks` — 내 서재 조회 [→ 사용자 활동 서비스]  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/web/LibraryController.java
- [x] `POST /api/v1/cards/{cardId}/comments` — 댓글 작성 [→ 사용자 활동 서비스]  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/web/CommentController.java
- [x] `POST /api/v1/comments/{commentId}/report` — 댓글 신고 [→ 사용자 활동 서비스] (사용자 확인: 사유 없이 신고만)  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/web/CommentController.java
- [x] `PATCH /api/v1/comments/{commentId}/visibility` — 댓글 숨김/해제 [→ 사용자 활동 서비스]  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/web/CommentController.java
- [ ] `GET /api/v1/rankings/weekly` — 주간 랭킹 조회 [→ 랭킹 API 서비스]  ←구현위치: 
- [x] `POST /api/v1/auth/login` — 로그인 [→ 인증 서비스]  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/web/AuthController.java
- [x] `POST /api/v1/auth/register` — 회원가입 [→ 인증 서비스]  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/web/AuthController.java

## Entities (11)
- [x] Entity `Answer` (속성 6개)  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/domain/Answer.java
- [x] Entity `AuditLog` (속성 8개)  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/AuditLog.java
- [x] Entity `Card` (속성 24개)  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/Card.java (읽기: BE/content-service/.../domain/Card.java)
- [x] Entity `Comment` (속성 8개)  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/Comment.java
- [x] Entity `Post` (속성 7개)  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/Post.java
- [ ] Entity `Project` (속성 9개)  ←구현위치: 
- [x] Entity `Question` (속성 7개)  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/domain/Question.java
- [ ] Entity `RankingSnapshot` (속성 5개)  ←구현위치: 
- [x] Entity `Reaction` (속성 5개)  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/Reaction.java
- [x] Entity `Submission` (속성 11개)  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/Submission.java
- [x] Entity `User` (속성 10개)  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/domain/User.java

## Policies (비즈니스 규칙) (36)
- [x] Policy `POL-01` — 모든 발행 카드 수정 이력은 카드에 기록되어야 한다.  ←구현위치: BE/curation-service/.../service/CardService.java + SubmissionService.java (AuditLog)
- [x] Policy `POL-02` — 댓글 작성은 사용자당 분당 3건으로 제한되어야 한다.  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/service/CommentService.java (PER_MINUTE_LIMIT=3 → COMMENT_RATE_LIMITED 429)
- [x] Policy `POL-03` — 제보는 계정당 하루 5건으로 제한되어야 한다.  ←구현위치: BE/curation-service/.../service/SubmissionService.java (DAILY_LIMIT=5 → SUBMISSION_LIMIT_EXCEEDED)
- [x] Policy `POL-04` — 좋아요 및 북마크는 계정당 카드당 1회만 가능하도록 저장 구조에서 강제되어야 한다.  ←구현위치: V1 uq_reactions_user_card_type + ReactionService 토글
- [x] Policy `POL-05` — URL 제보 시 잘못된 URL 형식은 422 Unprocessable Entity 오류로 거부되어야 한다.  ←구현위치: BE/curation-service/.../service/SubmissionService.java (validateUrl → INVALID_URL 422)
- [x] Policy `POL-06` — 게시글 제목은 5자 미만, 내용은 10자 미만일 수 없다.  ←구현위치: BE/curation-service/.../dto/PostDtos.java (@Size)
- [x] Policy `POL-07` — 댓글 내용은 최대 500자여야 한다.  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/dto/ActivityDtos.java (@Size max=500) + Comment length
- [x] Policy `POL-08` — 반려 시 반려 사유는 필수 입력되어야 한다.  ←구현위치: BE/curation-service/.../service/SubmissionService.java (REJECTION_REASON_REQUIRED)
- [x] Policy `POL-09` — 발행 시 4축 합계와 총점이 일치하지 않으면 발행을 거부해야 한다.  ←구현위치: BE/curation-service/.../domain/Card.java (총점=4축 합 강제)
- [x] Policy `POL-10` — 발행 카드 수정 시 실전점수 변경 사유는 필수 입력되어야 한다.  ←구현위치: BE/curation-service/.../service/CardService.java (SCORE_REASON_REQUIRED)
- [x] Policy `POL-11` — 질문 또는 답변 내용은 비어있을 수 없다.  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/dto/QnaDtos.java (@NotBlank)
- [ ] Policy `POL-12` — 프로젝트명은 2자 미만이거나 이미 존재하는 프로젝트명일 수 없다.  ←구현위치: 
- [x] Policy `POL-13` — 홈 피드 필터의 검색어는 1~50자 이내, 최소점수는 0~100 정수여야 한다.  ←구현위치: BE/content-service/.../service/CardQueryService.java (minScore 0~100) + curation PostService.search (검색어 ≤50)
- [x] Policy `POL-14` — 홈 피드 필터의 정렬 값은 '점수순', '최신순', '스타순' 중 하나여야 한다.  ←구현위치: BE/content-service/.../service/CardQueryService.java (SORTS 화이트리스트)
- [x] Policy `POL-15` — 가입 후 24시간이 지나지 않은 계정의 좋아요, 북마크, 댓글은 커뮤니티 점수 계산에서 제외되어야 한다.  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/support/AccountAgeChecker.java (카운터 미반영)
- [x] Policy `POL-16` — 검색 결과가 없을 경우 빈 목록과 함께 '검색 결과가 없습니다.' 메시지를 표시해야 한다.  ←구현위치: FE/mobile-web/src/views/HomeView.vue + PostsView.vue (빈 상태)
- [ ] Policy `POL-17` — 네트워크 지연으로 인한 발행 실패 시, 사용자에게 재시도 옵션을 제공해야 한다.  ←구현위치: 
- [x] Policy `POL-18` — 댓글 신고가 3건 쌓이면 시스템은 자동으로 댓글을 가리고 큐레이터 판단을 기다려야 한다.  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/Comment.java (addReport→3건 hidden) + CommentService
- [ ] Policy `POL-19` — 동점일 경우 북마크 수가 많은 카드가 상위로 배치되어야 한다.  ←구현위치: 
- [x] Policy `POL-20` — 로그인 없이 북마크 버튼 클릭 시 로그인 안내로 연결되어야 한다.  ←구현위치: FE/mobile-web/src/views/CardDetailView.vue (onReact→requireLogin)
- [ ] Policy `POL-21` — 메타 정보 갱신이 API 한도로 실패할 경우, 시스템은 마지막 값을 유지하고 메타 갱신일로 오래된 값임을 표시해야 한다.  ←구현위치: 
- [x] Policy `POL-22` — 반려된 카드는 내 서재에 '더 이상 공개되지 않는 카드예요'로 표시되어야 한다.  ←구현위치: FE/mobile-web/src/views/MyLibraryView.vue (status===REJECTED)
- [ ] Policy `POL-23` — 발행 7일이 지나지 않은 카드는 랭킹 산정에서 제외되어야 한다.  ←구현위치: 
- [x] Policy `POL-24` — 북마크한 카드가 없을 경우, '아직 저장한 카드가 없어요. 피드에서 북마크를 눌러보세요' 메시지와 피드로 가는 버튼을 표시해야 한다.  ←구현위치: FE/mobile-web/src/views/MyLibraryView.vue (빈 상태)
- [x] Policy `POL-25` — 비로그인 상태에서 댓글 작성 시도 시 로그인 안내로 연결되어야 한다.  ←구현위치: FE/mobile-web/src/views/CardDetailView.vue (submitComment→requireLogin)
- [ ] Policy `POL-26` — 원본 저장소가 삭제되거나 비공개로 변경되어 자동 갱신이 실패할 경우, 카드에 '원본 접근 불가' 뱃지를 표시하고 점수 갱신을 중단해야 한다.  ←구현위치: 
- [x] Policy `POL-27` — 존재하지 않는 카드 슬러그 요청 시 404 Not Found 오류를 반환해야 한다.  ←구현위치: BE/content-service/.../service/CardQueryService.java (CARD_NOT_FOUND 404)
- [x] Policy `POL-28` — 질문 삭제 시, 해당 질문에 달린 모든 답변도 함께 삭제되어야 한다.  ←구현위치: V1 answers FK ON DELETE CASCADE (구조적 강제)
- [ ] Policy `POL-29` — 프로젝트 삭제 시, 해당 프로젝트와 관련된 모든 데이터(게시글, 파일 등)는 아카이빙되거나 삭제되어야 한다.  ←구현위치: 
- [ ] Policy `POL-34` — OWASP Top 10 취약점에 대한 방어 체계를 갖춰야 한다.  ←구현위치: 
- [x] Policy `POL-35` — 검색어에 특수문자가 포함될 경우, SQL Injection 등의 공격을 방지하기 위해 안전하게 처리해야 한다.  ←구현위치: JPA 파라미터 바인딩(@Query :param) — 문자열 연결 없음
- [x] Policy `POL-36` — 게시글 작성 시 허용되지 않는 HTML 태그는 자동으로 제거하거나 이스케이프 처리해야 한다.  ←구현위치: BE/common/.../support/HtmlSanitizer.java (jsoup) + PostService
- [x] Policy `POL-37` — 로그인 5회 연속 실패 시 10분간 계정이 잠금 처리되어야 한다.  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/service/AuthService.java (MAX_LOGIN_ATTEMPTS=5, LOCK_DURATION=10m) + domain/User.registerFailedLogin
- [ ] Policy `POL-38` — 모든 사용자 데이터는 암호화되어 저장되어야 한다.  ←구현위치: 
- [x] Policy `POL-39` — 비밀번호는 최소 8자 이상이어야 한다.  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/dto/AuthDtos.java (RegisterRequest @Size min=8)
- [x] Policy `POL-40` — 세션은 7일간 유지되어야 한다.  ←구현위치: BE/auth-service/.../service/AuthService.java (refreshTtl=7d, session_expires_at) + common/JwtTokenProvider (refresh-ttl-seconds=604800)

## Policies — NFR (성능·비기능 규칙) (4)
> 아래 NFR 항목은 단일 구현 파일이 아니라 **검증 방법/증거**(부하테스트 명령·모니터링 설정·측정 문서 경로)를 마커 뒤에 적으세요 — 파일 경로 강요는 거짓 체크를 유도합니다.
- [ ] Policy `POL-30` — 검색 응답은 1초 이내에 완료되어야 한다.  ←구현위치: 
- [ ] Policy `POL-31` — 랭킹 배치는 카드 1,000장 기준 1분 이내에 완료되어야 한다.  ←구현위치: 
- [ ] Policy `POL-32` — 모든 API 응답 시간은 200ms 이내여야 한다.  ←구현위치: 
- [ ] Policy `POL-33` — 피드 첫 로딩은 2초 이내에 완료되어야 한다.  ←구현위치: 

## Screens (화면) (17)
- [ ] Screen `AI 모델/코드 공유` (`/models`) (→ API: API-04)  ←구현위치: 
- [x] Screen `게시글 목록` (`/posts`) (→ API: API-03, API-04)  ←구현위치: FE/mobile-web/src/views/PostsView.vue
- [x] Screen `게시글 상세` (`/posts/{postId}`) (→ API: API-16, API-14, API-15, API-04)  ←구현위치: FE/mobile-web/src/views/PostDetailView.vue (댓글/반응은 Phase 4)
- [x] Screen `게시글 작성/편집` (`/posts/new`) (→ API: API-03)  ←구현위치: FE/mobile-web/src/views/PostCreateView.vue
- [x] Screen `내 서재` (`/my-library`) (→ API: API-17)  ←구현위치: FE/mobile-web/src/views/MyLibraryView.vue
- [x] Screen `로그인/회원가입` (`/login`) (→ API: API-19, API-20)  ←구현위치: FE/mobile-web/src/views/LoginView.vue (실 API 연동: stores/auth.js + api/auth.js)
- [ ] Screen `메인 대시보드` (`/dashboard`) (→ API: API-18, API-02, API-04, API-01)  ←구현위치: 
- [ ] Screen `사용자 프로필` (`/users/{userId}`)  ←구현위치: 
- [x] Screen `제보하기` (`/submit`) (→ API: API-10)  ←구현위치: FE/mobile-web/src/views/SubmitView.vue
- [ ] Screen `주간 랭킹` (`/rankings/weekly`) (→ API: API-18)  ←구현위치: 
- [x] Screen `큐레이터 검수함` (`/admin/submissions`) (→ API: API-11)  ←구현위치: FE/admin-web/src/views/SubmissionsView.vue
- [x] Screen `카드 상세` (`/cards/{cardSlug}`) (→ API: API-16, API-13, API-02, API-15, API-12, API-14)  ←구현위치: FE/mobile-web/src/views/CardDetailView.vue (반응/댓글은 Phase 4)
- [ ] Screen `프로젝트 목록` (`/projects`) (→ API: API-07)  ←구현위치: 
- [ ] Screen `프로젝트 생성` (`/projects/new`) (→ API: API-07)  ←구현위치: 
- [ ] Screen `프로젝트 상세` (`/projects/{projectId}`) (→ API: API-09, API-08)  ←구현위치: 
- [x] Screen `홈 피드` (`/home`) (→ API: API-01, API-13)  ←구현위치: FE/mobile-web/src/views/HomeView.vue (반응은 Phase 4)
- [x] Screen `Q&A 목록` (`/qna`) (→ API: API-05)  ←구현위치: FE/mobile-web/src/views/QnaView.vue (+ QnaQuestionPanel)

## Aggregates (정합성 경계) (9)
- [x] Aggregate `Post` (불변식 3개) [→ 콘텐츠 서비스]  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/Post.java
- [x] Aggregate `Card` (불변식 3개) [→ 큐레이션 서비스]  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/Card.java
- [x] Aggregate `Submission` (불변식 3개) [→ 큐레이션 서비스]  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/Submission.java
- [ ] Aggregate `Project` (불변식 3개) [→ 프로젝트 서비스]  ←구현위치: 
- [x] Aggregate `Question` (불변식 1개) [→ Q&A 서비스]  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/domain/Question.java
- [ ] Aggregate `RankingSnapshot` (불변식 4개) [→ 랭킹 배치 워커]  ←구현위치: 
- [x] Aggregate `Comment` (불변식 4개) [→ 사용자 활동 서비스]  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/Comment.java
- [x] Aggregate `Reaction` (불변식 2개) [→ 사용자 활동 서비스]  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/Reaction.java
- [x] Aggregate `User` (불변식 4개) [→ 인증 서비스]  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/domain/User.java

## Domain Entities (데이터 모델) (2)
- [x] Domain Entity `AuditLog` (속성 7개)  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/AuditLog.java
- [x] Domain Entity `Answer` (속성 5개)  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/domain/Answer.java

## Domain Events (18)
- [x] Domain Event `PostCreated`  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/event/CurationEvents.java
- [x] Domain Event `AnswerCreated`  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/domain/event/QnaEvents.java
- [x] Domain Event `QuestionCreated`  ←구현위치: BE/qna-service/src/main/java/com/aicommunity/qna/domain/event/QnaEvents.java
- [ ] Domain Event `ProjectCreated`  ←구현위치: 
- [ ] Domain Event `ProjectUpdated`  ←구현위치: 
- [x] Domain Event `SubmissionReceived`  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/event/CurationEvents.java
- [x] Domain Event `CardPublished`  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/event/CurationEvents.java
- [x] Domain Event `CardRejected`  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/event/CurationEvents.java
- [x] Domain Event `CardUpdated`  ←구현위치: BE/curation-service/src/main/java/com/aicommunity/curation/domain/event/CurationEvents.java
- [x] Domain Event `ReactionToggled`  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/event/ActivityEvents.java
- [x] Domain Event `CommentCreated`  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/event/ActivityEvents.java
- [x] Domain Event `CommentHidden`  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/event/ActivityEvents.java
- [x] Domain Event `CommentReported`  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/event/ActivityEvents.java
- [x] Domain Event `CommentUnhidden`  ←구현위치: BE/user-activity-service/src/main/java/com/aicommunity/useractivity/domain/event/ActivityEvents.java
- [ ] Domain Event `RankingSnapshotGenerated`  ←구현위치: 
- [x] Domain Event `UserAccountLocked`  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/domain/event/UserEvents.java
- [x] Domain Event `UserLoggedIn`  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/domain/event/UserEvents.java
- [x] Domain Event `UserRegistered`  ←구현위치: BE/auth-service/src/main/java/com/aicommunity/auth/domain/event/UserEvents.java

## Services / Databases (12)
- [x] Service `관리자 웹 프론트엔드` (Vue.js)  ←구현위치: FE/admin-web/
- [x] Service `모바일 웹 프론트엔드` (Vue.js)  ←구현위치: FE/mobile-web/
- [x] Service `Q&A 서비스` (Spring Boot)  ←구현위치: BE/qna-service/
- [x] Service `랭킹 API 서비스` (Spring Boot)  ←구현위치: BE/ranking-api-service/
- [x] Service `사용자 활동 서비스` (Spring Boot)  ←구현위치: BE/user-activity-service/
- [x] Service `인증 서비스` (Spring Boot)  ←구현위치: BE/auth-service/
- [x] Service `콘텐츠 서비스` (Spring Boot)  ←구현위치: BE/content-service/
- [x] Service `큐레이션 서비스` (Spring Boot)  ←구현위치: BE/curation-service/
- [x] Service `프로젝트 서비스` (Spring Boot)  ←구현위치: BE/project-service/
- [x] Service `랭킹 배치 워커` (Spring Boot)  ←구현위치: BE/ranking-batch-worker/
- [x] Database `주 데이터베이스` (PostgreSQL)  ←구현위치: docker-compose.dev.yml (docker) / BE/common/src/main/resources/db/migration/V1__init_schema.sql (스키마) / local=H2 PostgreSQL모드
- [x] Database `캐시 및 세션 저장소` (Redis)  ←구현위치: docker-compose.dev.yml (docker) / local=인메모리 대체 (Phase 2+ 구현)
