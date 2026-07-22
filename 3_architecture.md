## 0. 명세 충실도 (Lineage Health)
- Service ↔ Aggregate 매핑: 7 / 8
- Service ↔ Story 매핑: 10 / 10
- **Service deployment 명시**: 7 / 8
- **Connection auth 명시**: 26 / 26
- Lineage confidence 분포: direct: 8, inferred: 4, none: 0
- API ↔ Service 매핑: 20 항목.
- ⚠️ 고립된 Service (랭킹 API 서비스) 존재 — 통신 경로 단절. 에이전트가 임의 통신 결정.

## 1. System Overview
이 시스템은 총 10개의 서비스와 2개의 데이터베이스로 구성된 AI 커뮤니티 플랫폼입니다. 사용자 인터페이스(프론트엔드)를 통해 백엔드 API 서비스와 상호작용하며, 백엔드 서비스들은 관계형 데이터베이스와 인메모리 캐시/세션 저장소를 활용하여 데이터를 관리합니다. 주요 데이터 흐름은 사용자 활동, 콘텐츠 관리, Q&A, 프로젝트 관리, 큐레이션 및 랭킹 생성으로 이루어집니다.

## 2. Service Layer

### 관리자 웹 프론트엔드 (ID: SVC-01, type: Frontend)
- **Tech Stack**: Vue.js
- **역할**: 큐레이터 및 관리자를 위한 웹 인터페이스. 제보 검수, 콘텐츠 관리 등 관리 기능을 제공한다.
- **책임 Aggregate (owned_aggregates)**: (Frontend — 서버 Aggregate 를 소유하지 않음. 화면·상태 모델은 SPACK 문서의 Screens 와 이 서비스가 호출하는 API 응답 스키마를 기준으로 구성)
- **PRD 추적성**:
  - `confidence`: inferred
  - `related_stories`:
    - story_04_3: "큐레이터가 [카드 상세] 화면에서 [수정] 버튼을 클릭한다."
    - story_04_2: "큐레이터가 [큐레이터 검수함] 화면에 접속한다."
- **배포 (Deployment)**:
  - `Port`: 0
  - `Replicas`: 1
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: manual
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 사용자 활동 서비스 (HTTPS/REST, Auth: bearer)
  - 인증 서비스 (HTTPS/REST, Auth: bearer)
  - 큐레이션 서비스 (HTTPS/REST, Auth: bearer)
- **수신 (incoming)**:
  (없음)

### 모바일 웹 프론트엔드 (ID: SVC-02, type: Frontend)
- **Tech Stack**: Vue.js
- **역할**: AI 지식 공유 및 협업 플랫폼의 모바일 웹 사용자 인터페이스.
- **책임 Aggregate (owned_aggregates)**: (Frontend — 서버 Aggregate 를 소유하지 않음. 화면·상태 모델은 SPACK 문서의 Screens 와 이 서비스가 호출하는 API 응답 스키마를 기준으로 구성)
- **PRD 추적성**:
  - `confidence`: inferred
  - `related_stories`:
    - story_01_1: "사용자가 [게시글 목록] 화면에서 [새 게시글 작성] 버튼을 클릭한다."
    - story_01_4: "사용자가 [홈 피드] 화면에 접속한다."
    - story_07_1: "사용자가 [로그인] 화면에 접속한다."
- **배포 (Deployment)**:
  - `Port`: 0
  - `Replicas`: 2
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: auto-cpu
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 프로젝트 서비스 (HTTPS/REST, Auth: bearer)
  - 콘텐츠 서비스 (HTTPS/REST, Auth: bearer)
  - 랭킹 API 서비스 (HTTPS/REST, Auth: bearer)
  - 사용자 활동 서비스 (HTTPS/REST, Auth: bearer)
  - Q&A 서비스 (HTTPS/REST, Auth: bearer)
  - 큐레이션 서비스 (HTTPS/REST, Auth: bearer)
  - 인증 서비스 (HTTPS/REST, Auth: bearer)
- **수신 (incoming)**:
  (없음)

### Q&A 서비스 (ID: SVC-03, type: Backend API)
- **Tech Stack**: Spring Boot
- **역할**: AI 기술 관련 질문 등록 및 답변 기능을 제공하는 서비스.
- **책임 Aggregate (owned_aggregates)**: Question
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_02_1: "사용자는 질문을 등록하고 다른 사용자의 답변을 받거나, 다른 사용자의 질문에 답변하여 지식을 공유하고 문제를 해결한다."
- **배포 (Deployment)**:
  - `Port`: 8080
  - `Replicas`: 2
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: auto-cpu
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 주 데이터베이스 (JDBC/TCP, Auth: basic)
  - 캐시 및 세션 저장소 (TCP, Auth: basic)
- **수신 (incoming)**:
  - 모바일 웹 프론트엔드 (HTTPS/REST, Auth: bearer)

### 랭킹 API 서비스 (ID: SVC-04, type: Backend API)
- **Tech Stack**: Spring Boot
- **역할**: 주간 랭킹 데이터를 조회하여 제공하는 서비스. 랭킹 스냅샷은 랭킹 배치 워커에 의해 생성된다.
- **책임 Aggregate (owned_aggregates)**: ⚠️ 미명시 — 서비스 책임 경계 불명.
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_06_1: "사용자는 주간 랭킹 화면에 접속하여 최신 트렌드를 파악한다."
- **배포 (Deployment)**:
  - `Port`: 8080
  - `Replicas`: 2
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: auto-cpu
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 캐시 및 세션 저장소 (TCP, Auth: basic)
  - 주 데이터베이스 (JDBC/TCP, Auth: basic)
- **수신 (incoming)**:
  - 모바일 웹 프론트엔드 (HTTPS/REST, Auth: bearer)

### 사용자 활동 서비스 (ID: SVC-05, type: Backend API)
- **Tech Stack**: Spring Boot
- **역할**: 좋아요, 북마크, 댓글 작성 및 관리 등 사용자 활동을 처리하는 서비스.
- **책임 Aggregate (owned_aggregates)**: Comment, Reaction
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_05_1: "회원은 관심 있는 AI 레포/스킬에 '좋아요'를 표시하거나 '북마크'하여 개인의 관심사를 표현하고 정보를 저장한다."
    - story_05_2: "회원은 내 서재에 접속하여 저장된 카드 목록을 확인한다."
    - story_05_3: "회원은 댓글을 작성하여 다른 사용자와 소통한다."
- **배포 (Deployment)**:
  - `Port`: 8080
  - `Replicas`: 2
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: auto-cpu
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 캐시 및 세션 저장소 (TCP, Auth: basic)
  - 주 데이터베이스 (JDBC/TCP, Auth: basic)
- **수신 (incoming)**:
  - 관리자 웹 프론트엔드 (HTTPS/REST, Auth: bearer)
  - 모바일 웹 프론트엔드 (HTTPS/REST, Auth: bearer)

### 인증 서비스 (ID: SVC-06, type: Backend API)
- **Tech Stack**: Spring Boot
- **역할**: 사용자 회원가입, 로그인, 세션 관리 및 권한 부여를 담당하는 서비스.
- **책임 Aggregate (owned_aggregates)**: User
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_07_1: "방문자는 회원가입하고 로그인하여 회원 권한을 획득한다."
- **배포 (Deployment)**:
  - `Port`: 8080
  - `Replicas`: 3
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: auto-cpu
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 캐시 및 세션 저장소 (TCP, Auth: basic)
  - 주 데이터베이스 (JDBC/TCP, Auth: basic)
- **수신 (incoming)**:
  - 관리자 웹 프론트엔드 (HTTPS/REST, Auth: bearer)
  - 모바일 웹 프론트엔드 (HTTPS/REST, Auth: bearer)

### 콘텐츠 서비스 (ID: SVC-07, type: Backend API)
- **Tech Stack**: Spring Boot
- **역할**: 게시글 작성, 검색, 필터링 등 AI 관련 지식 공유 콘텐츠를 관리하는 서비스.
- **책임 Aggregate (owned_aggregates)**: Post
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_01_2: "사용자는 검색창에 키워드를 입력하거나 태그로 필터링하여 원하는 정보를 효율적으로 탐색한다."
    - story_01_1: "사용자는 새 게시글을 작성하고 발행하여 다른 사용자들과 정보를 교류한다."
- **배포 (Deployment)**:
  - `Port`: 8080
  - `Replicas`: 2
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: auto-cpu
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 캐시 및 세션 저장소 (TCP, Auth: basic)
  - 주 데이터베이스 (JDBC/TCP, Auth: basic)
- **수신 (incoming)**:
  - 모바일 웹 프론트엔드 (HTTPS/REST, Auth: bearer)

### 큐레이션 서비스 (ID: SVC-08, type: Backend API)
- **Tech Stack**: Spring Boot
- **역할**: AI 레포/스킬 카드 정보 관리, 제보 검수, 발행 및 수정 기능을 제공하는 서비스.
- **책임 Aggregate (owned_aggregates)**: Card, Submission
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_01_4: "사용자는 홈 피드에 접속하여 카드 그리드를 확인하고, 카테고리, 최소점수, 정렬 기준으로 필터링하여 관심 있는 정보를 빠르게 탐색한다."
    - story_01_3: "사용자는 카드 상세 화면으로 이동하여 요약 전문, 4축 점수, 커뮤니티 반응 등을 확인한다."
    - story_04_1: "회원은 URL을 제보하여 커뮤니티에 기여한다."
    - story_04_3: "큐레이터는 발행된 AI 레포/스킬 카드의 정보를 수정하여 최신 정보를 유지한다."
    - story_04_2: "큐레이터는 회원이 제보한 AI 레포/스킬을 검수하고 요약, 카테고리, 문서화 점수를 확정하여 발행하거나 반려한다."
- **배포 (Deployment)**:
  - `Port`: 8080
  - `Replicas`: 2
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: auto-cpu
- **외부 의존성 (External Dependencies)**:
  | 이름 | 종류 | 용도 |
  |------|------|------|
  | GitHub API | External API | 제보된 URL의 메타 정보 자동 수집 |
- **CONNECTS_TO (outgoing)**:
  - 주 데이터베이스 (JDBC/TCP, Auth: basic)
  - 캐시 및 세션 저장소 (TCP, Auth: basic)
- **수신 (incoming)**:
  - 관리자 웹 프론트엔드 (HTTPS/REST, Auth: bearer)
  - 모바일 웹 프론트엔드 (HTTPS/REST, Auth: bearer)

### 프로젝트 서비스 (ID: SVC-09, type: Backend API)
- **Tech Stack**: Spring Boot
- **역할**: AI 관련 프로젝트 생성, 관리, 진행 상황 업데이트를 담당하는 서비스.
- **책임 Aggregate (owned_aggregates)**: Project
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_03_1: "사용자는 새 프로젝트를 생성하거나 기존 프로젝트에 참여하여 다른 사용자들과 협력적으로 아이디어를 발전시키고 결과물을 공유한다."
    - story_03_2: "프로젝트 관리자는 프로젝트 진행 상황을 업데이트하여 팀원 및 관심 사용자에게 최신 정보를 제공한다."
- **배포 (Deployment)**:
  - `Port`: 8080
  - `Replicas`: 2
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: auto-cpu
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 주 데이터베이스 (JDBC/TCP, Auth: basic)
  - 캐시 및 세션 저장소 (TCP, Auth: basic)
- **수신 (incoming)**:
  - 모바일 웹 프론트엔드 (HTTPS/REST, Auth: bearer)

### 랭킹 배치 워커 (ID: SVC-10, type: Background Worker)
- **Tech Stack**: Spring Boot
- **역할**: 주간 랭킹 스냅샷을 생성하고 업데이트하는 백그라운드 작업자.
- **책임 Aggregate (owned_aggregates)**: RankingSnapshot
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_06_1: "주간 랭킹은 매주 월요일 0시에 서버 배치가 지난 7일간의 데이터를 기반으로 생성한 스냅샷을 보여줘야 한다."
- **배포 (Deployment)**:
  - `Port`: 0
  - `Replicas`: 1
  - `Health check`: (없음)
  - `Required env vars`: (없음)
  - `Scaling`: manual
- **외부 의존성 (External Dependencies)**:
  (없음)
- **CONNECTS_TO (outgoing)**:
  - 캐시 및 세션 저장소 (TCP, Auth: basic)
  - 주 데이터베이스 (JDBC/TCP, Auth: basic)
- **수신 (incoming)**:
  (없음)

## 3. Data Layer

### 주 데이터베이스 (ID: DB-01, type: Relational Database)
- **Tech Stack**: PostgreSQL
- **역할**: 모든 서비스의 영속적인 데이터를 저장하는 주 관계형 데이터베이스. 사용자, 게시글, 카드, 프로젝트, Q&A, 댓글, 반응, 랭킹 스냅샷 등 핵심 비즈니스 데이터를 포함한다.
- **PRD 추적성**:
  - `confidence`: inferred
  - `related_stories`:
    - story_01_1: "`게시글` 테이블에 새 레코드 추가, `사용자`의 `작성 게시글 수` 카운트 증가."
    - story_07_1: "`사용자` 테이블에 새 레코드 추가/조회, `로그인 실패 횟수`, `잠금 해제 시간`, `세션 만료일` 필드 업데이트."
- **접근 서비스 (incoming)**:
  - Q&A 서비스 (JDBC/TCP)
  - 랭킹 API 서비스 (JDBC/TCP)
  - 사용자 활동 서비스 (JDBC/TCP)
  - 인증 서비스 (JDBC/TCP)
  - 콘텐츠 서비스 (JDBC/TCP)
  - 큐레이션 서비스 (JDBC/TCP)
  - 프로젝트 서비스 (JDBC/TCP)
  - 랭킹 배치 워커 (JDBC/TCP)

### 캐시 및 세션 저장소 (ID: DB-02, type: In-memory Database)
- **Tech Stack**: Redis
- **역할**: 고속 데이터 캐싱, 사용자 세션 관리, 동시성 제어, 실시간 카운터 및 랭킹 임시 데이터 저장을 위한 인메모리 데이터베이스.
- **PRD 추적성**:
  - `confidence`: inferred
  - `related_stories`:
    - story_07_1: "로그인 5회 연속 실패 시 10분간 계정이 잠금 처리되어야 한다."
    - story_04_1: "제보는 계정당 하루 5건으로 제한되며, 초과 시 '내일 다시 제보할 수 있어요' 메시지를 반환해야 한다."
    - story_05_3: "댓글 작성은 사용자당 분당 3건으로 제한되어야 한다."
- **접근 서비스 (incoming)**:
  - Q&A 서비스 (TCP)
  - 랭킹 API 서비스 (TCP)
  - 사용자 활동 서비스 (TCP)
  - 인증 서비스 (TCP)
  - 콘텐츠 서비스 (TCP)
  - 큐레이션 서비스 (TCP)
  - 프로젝트 서비스 (TCP)
  - 랭킹 배치 워커 (TCP)

## 4. Connection Map
| From | To | Protocol | Auth | 설명 |
|---|---|---|---|---|
| 관리자 웹 프론트엔드 | 사용자 활동 서비스 | HTTPS/REST | bearer | 댓글 숨김/해제 |
| 관리자 웹 프론트엔드 | 인증 서비스 | HTTPS/REST | bearer | 관리자 로그인 및 인증 |
| 관리자 웹 프론트엔드 | 큐레이션 서비스 | HTTPS/REST | bearer | 제보 검수 및 카드 관리 |
| 모바일 웹 프론트엔드 | 프로젝트 서비스 | HTTPS/REST | bearer | 프로젝트 생성 및 관리 |
| 모바일 웹 프론트엔드 | 콘텐츠 서비스 | HTTPS/REST | bearer | 게시글 작성 및 조회 |
| 모바일 웹 프론트엔드 | 랭킹 API 서비스 | HTTPS/REST | bearer | 주간 랭킹 조회 |
| 모바일 웹 프론트엔드 | 사용자 활동 서비스 | HTTPS/REST | bearer | 좋아요, 북마크, 댓글 |
| 모바일 웹 프론트엔드 | Q&A 서비스 | HTTPS/REST | bearer | 질문 및 답변 |
| 모바일 웹 프론트엔드 | 큐레이션 서비스 | HTTPS/REST | bearer | 카드 상세, 홈 피드, 제보 |
| 모바일 웹 프론트엔드 | 인증 서비스 | HTTPS/REST | bearer | 사용자 로그인 및 회원가입 |
| Q&A 서비스 | 주 데이터베이스 | JDBC/TCP | basic | 질문 및 답변 정보 Read/Write |
| Q&A 서비스 | 캐시 및 세션 저장소 | TCP | basic | Q&A 캐싱 |
| 랭킹 API 서비스 | 캐시 및 세션 저장소 | TCP | basic | 랭킹 데이터 캐싱 |
| 랭킹 API 서비스 | 주 데이터베이스 | JDBC/TCP | basic | 주간 랭킹 스냅샷 Read |
| 사용자 활동 서비스 | 캐시 및 세션 저장소 | TCP | basic | 댓글 캐싱, 댓글 작성율 제한 |
| 사용자 활동 서비스 | 주 데이터베이스 | JDBC/TCP | basic | 좋아요, 북마크, 댓글 정보 Read/Write |
| 인증 서비스 | 캐시 및 세션 저장소 | TCP | basic | 로그인 시도 횟수, 세션 정보 저장 |
| 인증 서비스 | 주 데이터베이스 | JDBC/TCP | basic | 사용자 정보 Read/Write |
| 콘텐츠 서비스 | 캐시 및 세션 저장소 | TCP | basic | 게시글 캐싱 |
| 콘텐츠 서비스 | 주 데이터베이스 | JDBC/TCP | basic | 게시글 정보 Read/Write |
| 큐레이션 서비스 | 주 데이터베이스 | JDBC/TCP | basic | 카드 및 제보 정보 Read/Write |
| 큐레이션 서비스 | 캐시 및 세션 저장소 | TCP | basic | 카드 캐싱, 제보율 제한 |
| 프로젝트 서비스 | 주 데이터베이스 | JDBC/TCP | basic | 프로젝트 정보 Read/Write |
| 프로젝트 서비스 | 캐시 및 세션 저장소 | TCP | basic | 프로젝트 캐싱 |
| 랭킹 배치 워커 | 캐시 및 세션 저장소 | TCP | basic | 랭킹 계산 임시 데이터 |
| 랭킹 배치 워커 | 주 데이터베이스 | JDBC/TCP | basic | 랭킹 산정 데이터 Read 및 스냅샷 Write |

## 5. API ↔ Service Mapping
| API | Service | 배치 사유 |
|---|---|---|
| POST /api/v1/questions — 질문 등록 | Q&A 서비스 | Question 도메인 비즈니스 로직 담당 |
| POST /api/v1/questions/{questionId}/answers — 답변 등록 | Q&A 서비스 | Question 도메인 비즈니스 로직 담당 |
| GET /api/v1/rankings/weekly — 주간 랭킹 조회 | 랭킹 API 서비스 | RankingSnapshot 도메인 비즈니스 로직 담당 |
| POST /api/v1/cards/{cardId}/reactions — 좋아요 및 북마크 토글 | 사용자 활동 서비스 | Reaction 도메인 비즈니스 로직 담당 |
| GET /api/v1/users/{userId}/bookmarks — 내 서재 조회 | 사용자 활동 서비스 | Comment 도메인 비즈니스 로직 담당 |
| PATCH /api/v1/comments/{commentId}/visibility — 댓글 숨김/해제 | 사용자 활동 서비스 | Comment 도메인 비즈니스 로직 담당 |
| POST /api/v1/comments/{commentId}/report — 댓글 신고 | 사용자 활동 서비스 | Comment 도메인 비즈니스 로직 담당 |
| POST /api/v1/cards/{cardId}/comments — 댓글 작성 | 사용자 활동 서비스 | Reaction 도메인 비즈니스 로직 담당 |
| POST /api/v1/auth/register — 회원가입 | 인증 서비스 | User 도메인 비즈니스 로직 담당 |
| POST /api/v1/auth/login — 로그인 | 인증 서비스 | User 도메인 비즈니스 로직 담당 |
| GET /api/v1/cards — 홈 피드 카드 목록 조회 및 필터링 | 콘텐츠 서비스 | Post 도메인 비즈니스 로직 담당 |
| GET /api/v1/cards/{cardSlug} — 카드 상세 조회 | 콘텐츠 서비스 | Post 도메인 비즈니스 로직 담당 |
| POST /api/v1/submissions — URL 제보 | 큐레이션 서비스 | Submission 도메인 비즈니스 로직 담당 |
| PATCH /api/v1/cards/{cardId} — 발행 카드 수정 | 큐레이션 서비스 | Card 도메인 비즈니스 로직 담당 |
| PATCH /api/v1/submissions/{submissionId}/review — 제보 검수 및 발행/반려 | 큐레이션 서비스 | Submission 도메인 비즈니스 로직 담당 |
| GET /api/v1/posts — 게시글 검색 및 필터링 | 큐레이션 서비스 | Card 도메인 비즈니스 로직 담당 |
| POST /api/v1/posts — 게시글 작성 | 큐레이션 서비스 | Card 도메인 비즈니스 로직 담당 |
| POST /api/v1/projects — 프로젝트 생성 | 프로젝트 서비스 | Project 도메인 비즈니스 로직 담당 |
| PATCH /api/v1/projects/{projectId}/status — 프로젝트 진행 상황 업데이트 | 프로젝트 서비스 | Project 도메인 비즈니스 로직 담당 |
| PUT /api/v1/projects/{projectId} — 프로젝트 수정 | 프로젝트 서비스 | Project 도메인 비즈니스 로직 담당 |

## 6. 구현 체크리스트

### 관리자 웹 프론트엔드 (SVC-01)
- [ ] 프로젝트 셋업 (`Vue.js` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (CONNECTS_TO 기준)
- [ ] 데이터 영속화 (Database 연결 없음)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 모바일 웹 프론트엔드 (SVC-02)
- [ ] 프로젝트 셋업 (`Vue.js` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (CONNECTS_TO 기준)
- [ ] 데이터 영속화 (Database 연결 없음)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### Q&A 서비스 (SVC-03)
- [ ] 프로젝트 셋업 (`Spring Boot` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (없음)
- [ ] 데이터 영속화 (Database 연결)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 랭킹 API 서비스 (SVC-04)
- [ ] 프로젝트 셋업 (`Spring Boot` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (없음)
- [ ] 데이터 영속화 (Database 연결)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 사용자 활동 서비스 (SVC-05)
- [ ] 프로젝트 셋업 (`Spring Boot` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (없음)
- [ ] 데이터 영속화 (Database 연결)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 인증 서비스 (SVC-06)
- [ ] 프로젝트 셋업 (`Spring Boot` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (없음)
- [ ] 데이터 영속화 (Database 연결)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 콘텐츠 서비스 (SVC-07)
- [ ] 프로젝트 셋업 (`Spring Boot` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (없음)
- [ ] 데이터 영속화 (Database 연결)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 큐레이션 서비스 (SVC-08)
- [ ] 프로젝트 셋업 (`Spring Boot` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (없음)
- [ ] 데이터 영속화 (Database 연결)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 프로젝트 서비스 (SVC-09)
- [ ] 프로젝트 셋업 (`Spring Boot` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (없음)
- [ ] 데이터 영속화 (Database 연결)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 랭킹 배치 워커 (SVC-10)
- [ ] 프로젝트 셋업 (`Spring Boot` 환경 구성)
- [ ] 책임 Aggregate 의 도메인 모델 통합 (DDD 문서 참조)
- [ ] 외부 통신 클라이언트 구현 (없음)
- [ ] 데이터 영속화 (Database 연결)
- [ ] 헬스체크 / 로깅 / 메트릭
- [ ] 컨테이너화 + CI/CD

### 주 데이터베이스 (DB-01)
- [ ] 인스턴스 프로비저닝
- [ ] 스키마 마이그레이션
- [ ] 백업/복구 전략

### 캐시 및 세션 저장소 (DB-02)
- [ ] 인스턴스 프로비저닝
- [ ] 스키마 마이그레이션
- [ ] 백업/복구 전략