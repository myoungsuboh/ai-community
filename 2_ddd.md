0. 명세 충실도 (Lineage Health)
- Aggregate ↔ Story 매핑: 9 / 9 (100.0%)
- Event ↔ Story 매핑: 18 / 18 (100.0%)
- Aggregate invariants 명시: 9 / 9 (100.0%)
- DomainEntity attributes 명시: 2 / 2 (100.0%)
- DomainEvent payload 명시: 18 / 18 (100.0%)
- Lineage confidence 분포: direct: 11, inferred: 0, none: 0
- 껍데기 Aggregate: 없음

1. Domain Overview
- **Q&A 컨텍스트**: AI 기술 관련 질문 및 답변 기능을 담당하는 바운디드 컨텍스트
- **랭킹 컨텍스트**: 주간 랭킹 및 통계 기능을 담당하는 바운디드 컨텍스트
- **사용자 컨텍스트**: 사용자 계정 인증 및 권한 관리 기능을 담당하는 바운디드 컨텍스트
- **사용자 활동 컨텍스트**: 사용자 활동(좋아요, 북마크, 댓글) 및 개인화 기능을 담당하는 바운디드 컨텍스트
- **콘텐츠 컨텍스트**: AI 관련 지식 공유 및 탐색 기능을 담당하는 바운디드 컨텍스트
- **큐레이션 컨텍스트**: AI 정보 제보 및 큐레이터 검수/발행 기능을 담당하는 바운디드 컨텍스트
- **프로젝트 컨텍스트**: AI 관련 프로젝트 협업 및 관리 기능을 담당하는 바운디드 컨텍스트

2. Bounded Context별 상세

### Q&A 컨텍스트
- **책임 범위**: AI 기술 관련 질문 및 답변 기능을 담당하는 바운디드 컨텍스트

#### Aggregates
- **Question** (ID: AGG-05)
- 책임: AI 기술 관련 질문 정보 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_02_1: "AI 기술 관련 궁금증이 생겼을 때 질문을 등록"
- **도메인 규칙 (Invariants)**:
  - `질문 내용은 비어있을 수 없습니다.`
- 소속 Domain Entities: Answer
- 발행 Domain Events: AnswerCreated, QuestionCreated

#### Domain Entities
- **Answer** (ID: DENT-02, 소속 Aggregate: Question)
- 설명: Q&A 질문에 대한 답변 정보
- PRD 추적성:
  - `confidence`: direct
  - `related_stories`:
    - story_02_1: "다른 사용자의 질문에 답변"
- **속성 (Attributes)**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | id | uuid | true | | |
  | questionId | uuid | true | | |
  | content | string | true | | |
  | authorId | uuid | true | | |
  | createdAt | datetime | true | | |

  > 같은 엔티티가 1_spack.md 에도 있으면 속성 충돌 시 **이 표가 권위**입니다 — 1_spack.md 쪽 표는 참조용.

#### Domain Events
- **AnswerCreated** (ID: EVT-02)
- 설명: 답변이 성공적으로 등록됨
- 발행 Aggregate: Question
- 트리거 Story: Story-02.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | answerId | uuid | true | | |
  | questionId | uuid | true | | |
  | authorId | uuid | true | | |
  | createdAt | datetime | true | | |

- **QuestionCreated** (ID: EVT-03)
- 설명: 질문이 성공적으로 등록됨
- 발행 Aggregate: Question
- 트리거 Story: Story-02.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | questionId | uuid | true | | |
  | authorId | uuid | true | | |
  | title | string | true | | |
  | createdAt | datetime | true | | |

### 랭킹 컨텍스트
- **책임 범위**: 주간 랭킹 및 통계 기능을 담당하는 바운디드 컨텍스트

#### Aggregates
- **RankingSnapshot** (ID: AGG-06)
- 책임: 주간 랭킹 데이터를 저장하는 스냅샷 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_06_1: "이번 주 인기 AI 레포/스킬을 확인하고 싶을 때 주간 랭킹 화면에 접속"
- **도메인 규칙 (Invariants)**:
  - `주간 랭킹은 매주 월요일 0시에 생성됩니다.`
  - `랭킹 산정식: 좋아요 1점, 북마크 2점, 댓글 1점 합산 + 실전점수의 10분의 1.`
  - `동점일 경우 북마크 수가 많은 카드가 상위로 배치됩니다.`
  - `발행 7일이 지나지 않은 카드는 랭킹 산정에서 제외됩니다.`
- 소속 Domain Entities: (없음)
- 발행 Domain Events: RankingSnapshotGenerated

#### Domain Entities
- (없음)

#### Domain Events
- **RankingSnapshotGenerated** (ID: EVT-15)
- 설명: 주간 랭킹 스냅샷이 생성됨
- 발행 Aggregate: RankingSnapshot
- 트리거 Story: Story-06.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | snapshotId | uuid | true | | |
  | weekOfYear | integer | true | | |
  | year | integer | true | | |
  | generatedAt | datetime | true | | |

### 사용자 컨텍스트
- **책임 범위**: 사용자 계정 인증 및 권한 관리 기능을 담당하는 바운디드 컨텍스트

#### Aggregates
- **User** (ID: AGG-09)
- 책임: 플랫폼 사용자 계정 정보 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_07_1: "서비스의 모든 기능을 이용하고 싶을 때 회원가입하고 로그인"
- **도메인 규칙 (Invariants)**:
  - `비밀번호는 최소 8자 이상이어야 합니다.`
  - `로그인 5회 연속 실패 시 10분간 계정이 잠금 처리됩니다.`
  - `세션은 7일간 유지되어야 합니다.`
  - `회원가입 시 닉네임은 필수 입력되어야 합니다.`
- 소속 Domain Entities: (없음)
- 발행 Domain Events: UserAccountLocked, UserLoggedIn, UserRegistered

#### Domain Entities
- (없음)

#### Domain Events
- **UserAccountLocked** (ID: EVT-16)
- 설명: 계정이 잠금 처리됨
- 발행 Aggregate: User
- 트리거 Story: Story-07.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | userId | uuid | true | | |
  | lockedUntil | datetime | true | | |
  | lockedAt | datetime | true | | |

- **UserLoggedIn** (ID: EVT-17)
- 설명: 회원이 성공적으로 로그인됨
- 발행 Aggregate: User
- 트리거 Story: Story-07.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | userId | uuid | true | | |
  | loggedInAt | datetime | true | | |

- **UserRegistered** (ID: EVT-18)
- 설명: 회원이 성공적으로 가입됨
- 발행 Aggregate: User
- 트리거 Story: Story-07.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | userId | uuid | true | | |
  | email | string | true | | |
  | nickname | string | true | | |
  | registeredAt | datetime | true | | |

### 사용자 활동 컨텍스트
- **책임 범위**: 사용자 활동(좋아요, 북마크, 댓글) 및 개인화 기능을 담당하는 바운디드 컨텍스트

#### Aggregates
- **Comment** (ID: AGG-07)
- 책임: AI 레포/스킬 카드에 대한 사용자 댓글 정보 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_05_3: "AI 레포/스킬 카드에 대한 의견을 공유하고 싶을 때 댓글을 작성"
- **도메인 규칙 (Invariants)**:
  - `댓글 내용은 최대 500자여야 합니다.`
  - `사용자당 분당 3건으로 댓글 작성이 제한됩니다.`
  - `가입 후 24시간이 지나지 않은 계정의 댓글은 커뮤니티 점수 계산에서 제외됩니다.`
  - `댓글 신고가 3건 쌓이면 자동으로 댓글이 가려집니다.`
- 소속 Domain Entities: (없음)
- 발행 Domain Events: CommentCreated, CommentHidden, CommentReported, CommentUnhidden

- **Reaction** (ID: AGG-08)
- 책임: 사용자의 좋아요 및 북마크 반응 정보 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_05_1: "관심 있는 AI 레포/스킬에 '좋아요'를 표시하거나 '북마크'하여"
    - story_05_2: "북마크한 AI 레포/스킬을 모아보고 싶을 때 내 서재에 접속"
- **도메인 규칙 (Invariants)**:
  - `계정당 카드당 1회만 좋아요 및 북마크가 가능합니다.`
  - `가입 후 24시간이 지나지 않은 계정의 좋아요 및 북마크는 커뮤니티 점수 계산에서 제외됩니다.`
- 소속 Domain Entities: (없음)
- 발행 Domain Events: ReactionToggled

#### Domain Entities
- (없음)

#### Domain Events
- **CommentCreated** (ID: EVT-11)
- 설명: 댓글이 성공적으로 작성됨
- 발행 Aggregate: Comment
- 트리거 Story: Story-05.3
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | commentId | uuid | true | | |
  | cardId | uuid | true | | |
  | userId | uuid | true | | |
  | content | string | true | | |
  | createdAt | datetime | true | | |

- **CommentHidden** (ID: EVT-12)
- 설명: 댓글이 자동으로 가려지거나 큐레이터에 의해 숨김 확정됨
- 발행 Aggregate: Comment
- 트리거 Story: Story-05.3
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | commentId | uuid | true | | |
  | cardId | uuid | true | | |
  | hiddenBy | string | true | | |
  | hiddenAt | datetime | true | | |
  | reason | string | false | | |

- **CommentReported** (ID: EVT-13)
- 설명: 댓글이 신고됨
- 발행 Aggregate: Comment
- 트리거 Story: Story-05.3
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | commentId | uuid | true | | |
  | cardId | uuid | true | | |
  | reporterId | uuid | true | | |
  | reportedAt | datetime | true | | |

- **CommentUnhidden** (ID: EVT-14)
- 설명: 댓글 숨김이 해제됨
- 발행 Aggregate: Comment
- 트리거 Story: Story-05.3
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | commentId | uuid | true | | |
  | cardId | uuid | true | | |
  | unhiddenBy | uuid | true | | |
  | unhiddenAt | datetime | true | | |

- **ReactionToggled** (ID: EVT-10)
- 설명: 좋아요/북마크 상태가 변경됨
- 발행 Aggregate: Reaction
- 트리거 Story: Story-05.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | reactionId | uuid | true | | |
  | userId | uuid | true | | |
  | cardId | uuid | true | | |
  | type | string | true | | |
  | isAdded | boolean | true | | |
  | toggledAt | datetime | true | | |

### 콘텐츠 컨텍스트
- **책임 범위**: AI 관련 지식 공유 및 탐색 기능을 담당하는 바운디드 컨텍스트

#### Aggregates
- **Post** (ID: AGG-01)
- 책임: AI 관련 지식이나 정보를 공유하는 게시글 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_01_1: "새 게시글을 작성하고 발행"
    - story_01_2: "검색창에 키워드를 입력하거나 태그로 필터링"
- **도메인 규칙 (Invariants)**:
  - `제목은 5자 이상이어야 합니다.`
  - `내용은 10자 이상이어야 합니다.`
  - `허용되지 않는 HTML 태그는 제거되거나 이스케이프 처리되어야 합니다.`
- 소속 Domain Entities: (없음)
- 발행 Domain Events: PostCreated

#### Domain Entities
- (없음)

#### Domain Events
- **PostCreated** (ID: EVT-01)
- 설명: 게시글이 성공적으로 작성됨
- 발행 Aggregate: Post
- 트리거 Story: Story-01.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | postId | uuid | true | | |
  | authorId | uuid | true | | |
  | title | string | true | | |
  | createdAt | datetime | true | | |

### 큐레이션 컨텍스트
- **책임 범위**: AI 정보 제보 및 큐레이터 검수/발행 기능을 담당하는 바운디드 컨텍스트

#### Aggregates
- **Card** (ID: AGG-02)
- 책임: AI 레포/스킬의 상세 정보 및 커뮤니티 반응을 포함하는 카드 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_04_3: "발행된 AI 레포/스킬 카드의 정보가 변경되었을 때"
    - story_01_3: "AI 레포/스킬의 상세 정보와 실전점수 근거를 확인"
    - story_04_2: "회원이 제보한 AI 레포/스킬을 검수하고"
    - story_01_4: "매일 갱신되는 AI 레포/스킬을 한눈에 보고 싶을 때"
- **도메인 규칙 (Invariants)**:
  - `4축 합계와 총점이 일치해야 합니다.`
  - `원본 저장소가 접근 불가 시 '원본 접근 불가' 뱃지 표시 및 점수 갱신 중단.`
  - `실전점수 도넛 링은 70점 이상 초록, 40~69점 호박, 40점 미만 회색으로 표시.`
- 소속 Domain Entities: AuditLog
- 발행 Domain Events: CardPublished, CardRejected, CardUpdated

- **Submission** (ID: AGG-03)
- 책임: 회원이 제보한 AI 레포/스킬 URL 정보 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_04_1: "새로운 AI 레포/스킬 정보를 공유하고 싶을 때 URL을 제보"
- **도메인 규칙 (Invariants)**:
  - `계정당 하루 5건으로 제보가 제한됩니다.`
  - `같은 URL은 이미 발행되었거나 검수 대기 중일 수 없습니다.`
  - `3줄 요약 각 줄은 최대 60자여야 합니다.`
- 소속 Domain Entities: (없음)
- 발행 Domain Events: SubmissionReceived

#### Domain Entities
- **AuditLog** (ID: DENT-01, 소속 Aggregate: Card)
- 설명: 카드 데이터 변경 이력
- PRD 추적성:
  - `confidence`: direct
  - `related_stories`:
    - story_04_3: "모든 수정 이력은 카드에 기록되어야 한다."
- **속성 (Attributes)**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | id | uuid | true | | |
  | cardId | uuid | true | | |
  | modifierId | uuid | true | | |
  | actionType | string | true | | |
  | changedFields | string | false | | |
  | reason | string | false | | |
  | timestamp | datetime | true | | |

  > 같은 엔티티가 1_spack.md 에도 있으면 속성 충돌 시 **이 표가 권위**입니다 — 1_spack.md 쪽 표는 참조용.

#### Domain Events
- **CardPublished** (ID: EVT-07)
- 설명: 카드가 검수 후 발행됨
- 발행 Aggregate: Card
- 트리거 Story: Story-04.2
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | cardId | uuid | true | | |
  | submissionId | uuid | true | | |
  | curatorId | uuid | true | | |
  | publishedAt | datetime | true | | |

- **CardRejected** (ID: EVT-08)
- 설명: 카드가 검수 후 반려됨
- 발행 Aggregate: Card
- 트리거 Story: Story-04.2
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | cardId | uuid | true | | |
  | submissionId | uuid | true | | |
  | curatorId | uuid | true | | |
  | rejectionReason | string | true | | |
  | rejectedAt | datetime | true | | |

- **CardUpdated** (ID: EVT-09)
- 설명: 발행된 카드가 수정됨
- 발행 Aggregate: Card
- 트리거 Story: Story-04.3
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | cardId | uuid | true | | |
  | modifierId | uuid | true | | |
  | updatedFields | string | false | | |
  | updatedAt | datetime | true | | |
  | reason | string | false | | |

- **SubmissionReceived** (ID: EVT-06)
- 설명: URL 제보가 접수됨
- 발행 Aggregate: Submission
- 트리거 Story: Story-04.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | submissionId | uuid | true | | |
  | submitterId | uuid | true | | |
  | url | string | true | | |
  | createdAt | datetime | true | | |

### 프로젝트 컨텍스트
- **책임 범위**: AI 관련 프로젝트 협업 및 관리 기능을 담당하는 바운디드 컨텍스트

#### Aggregates
- **Project** (ID: AGG-04)
- 책임: AI 관련 프로젝트 협업 및 관리 정보 애그리거트
- **PRD 추적성**:
  - `confidence`: direct
  - `related_stories`:
    - story_03_1: "AI 관련 프로젝트를 시작하거나 참여하고 싶을 때"
    - story_03_2: "진행 중인 AI 프로젝트의 상황을 공유하고 싶을 때"
- **도메인 규칙 (Invariants)**:
  - `프로젝트명은 2자 이상이어야 합니다.`
  - `프로젝트명은 중복될 수 없습니다.`
  - `프로젝트 관리자만 프로젝트 진행 상황을 업데이트할 수 있습니다.`
- 소속 Domain Entities: (없음)
- 발행 Domain Events: ProjectCreated, ProjectUpdated

#### Domain Entities
- (없음)

#### Domain Events
- **ProjectCreated** (ID: EVT-04)
- 설명: 프로젝트가 성공적으로 생성됨
- 발행 Aggregate: Project
- 트리거 Story: Story-03.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | projectId | uuid | true | | |
  | creatorId | uuid | true | | |
  | name | string | true | | |
  | createdAt | datetime | true | | |

- **ProjectUpdated** (ID: EVT-05)
- 설명: 프로젝트 정보가 수정됨
- 발행 Aggregate: Project
- 트리거 Story: Story-03.1
- **Payload 필드**:

  | 필드 | 타입 | 필수 | 제약 | 설명 |
  |------|------|------|------|------|
  | projectId | uuid | true | | |
  | modifierId | uuid | true | | |
  | updatedFields | string | false | | |
  | updatedAt | datetime | true | | |

3. 구현 체크리스트
- [ ] Repository 인터페이스 (Aggregate 별)
- [ ] Domain Service 클래스
- [ ] Domain Event 발행 메커니즘 (in-process / Kafka 등 — Architecture 문서 참조)
- [ ] Event Handler (이벤트 수신 측이 있는 경우)
- [ ] 도메인 단위 테스트