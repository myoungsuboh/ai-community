-- ===========================================================================
-- V1 초기 스키마 — AI 커뮤니티 (Task 1.2 / 1.3)
-- 근거: _workspace/db_schema.json  (2_ddd.md 애그리거트/불변식 + API 규약)
-- 규칙: skills/db/er-er-modeling.md (snake_case 복수형, UUID PK, 감사컬럼, 인덱스)
-- 이식성: PostgreSQL 16 / H2(PostgreSQL 모드) 공용 SQL
-- 마이크로서비스 경계: 서비스 내부 관계에만 FK 선언, 서비스 간 참조는 ID(무FK).
-- ===========================================================================

-- ---------- 사용자 컨텍스트 (auth-service) ----------
CREATE TABLE users (
    id                 UUID PRIMARY KEY,
    email              VARCHAR(255) NOT NULL UNIQUE,
    password_hash      VARCHAR(255) NOT NULL,
    nickname           VARCHAR(50)  NOT NULL,
    role               VARCHAR(20)  NOT NULL DEFAULT 'MEMBER',
    failed_login_count INT          NOT NULL DEFAULT 0,
    locked_until       TIMESTAMP WITH TIME ZONE,
    session_expires_at TIMESTAMP WITH TIME ZONE,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- 콘텐츠 컨텍스트 (content-service) ----------
CREATE TABLE posts (
    id         UUID PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    author_id  UUID         NOT NULL,
    tags       TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_posts_author ON posts (author_id);
CREATE INDEX idx_posts_created_at ON posts (created_at);

-- ---------- 큐레이션 컨텍스트 (curation-service; 카드 읽기는 content-service 공유) ----------
CREATE TABLE cards (
    id                     UUID PRIMARY KEY,
    slug                   VARCHAR(160) NOT NULL UNIQUE,
    title                  VARCHAR(200) NOT NULL,
    category               VARCHAR(50)  NOT NULL,
    repo_url               VARCHAR(500) NOT NULL,
    summary_line1          VARCHAR(60),
    summary_line2          VARCHAR(60),
    summary_line3          VARCHAR(60),
    score_total            INT          NOT NULL DEFAULT 0,
    score_axis_docs        INT          NOT NULL DEFAULT 0,
    score_axis_activity    INT          NOT NULL DEFAULT 0,
    score_axis_popularity  INT          NOT NULL DEFAULT 0,
    score_axis_maintenance INT          NOT NULL DEFAULT 0,
    star_count             INT          NOT NULL DEFAULT 0,
    like_count             INT          NOT NULL DEFAULT 0,
    bookmark_count         INT          NOT NULL DEFAULT 0,
    comment_count          INT          NOT NULL DEFAULT 0,
    status                 VARCHAR(20)  NOT NULL DEFAULT 'PUBLISHED',
    source_accessible      BOOLEAN      NOT NULL DEFAULT TRUE,
    meta_updated_at        TIMESTAMP WITH TIME ZONE,
    submission_id          UUID,
    published_at           TIMESTAMP WITH TIME ZONE,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_cards_status ON cards (status);
CREATE INDEX idx_cards_category ON cards (category);
CREATE INDEX idx_cards_score_total ON cards (score_total);
CREATE INDEX idx_cards_published_at ON cards (published_at);

CREATE TABLE submissions (
    id               UUID PRIMARY KEY,
    url              VARCHAR(500) NOT NULL,
    submitter_id     UUID         NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    summary_line1    VARCHAR(60),
    summary_line2    VARCHAR(60),
    summary_line3    VARCHAR(60),
    category         VARCHAR(50),
    rejection_reason VARCHAR(500),
    reviewed_by      UUID,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_submissions_status ON submissions (status);
CREATE INDEX idx_submissions_submitter ON submissions (submitter_id);

CREATE TABLE audit_logs (
    id             UUID PRIMARY KEY,
    card_id        UUID         NOT NULL,
    modifier_id    UUID         NOT NULL,
    action_type    VARCHAR(30)  NOT NULL,
    changed_fields TEXT,
    reason         VARCHAR(500),
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_card FOREIGN KEY (card_id) REFERENCES cards (id)
);
CREATE INDEX idx_audit_logs_card ON audit_logs (card_id);

-- ---------- 사용자 활동 컨텍스트 (user-activity-service) ----------
CREATE TABLE reactions (
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL,
    card_id    UUID        NOT NULL,
    type       VARCHAR(10) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_reactions_user_card_type UNIQUE (user_id, card_id, type)
);
CREATE INDEX idx_reactions_user ON reactions (user_id);
CREATE INDEX idx_reactions_card ON reactions (card_id);

CREATE TABLE comments (
    id           UUID PRIMARY KEY,
    card_id      UUID         NOT NULL,
    user_id      UUID         NOT NULL,
    content      VARCHAR(500) NOT NULL,
    report_count INT          NOT NULL DEFAULT 0,
    is_hidden    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_comments_card ON comments (card_id);
CREATE INDEX idx_comments_user ON comments (user_id);

CREATE TABLE comment_reports (
    id          UUID PRIMARY KEY,
    comment_id  UUID NOT NULL,
    reporter_id UUID NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_reports_comment FOREIGN KEY (comment_id) REFERENCES comments (id),
    CONSTRAINT uq_comment_reports_once UNIQUE (comment_id, reporter_id)
);

-- ---------- Q&A 컨텍스트 (qna-service) ----------
CREATE TABLE questions (
    id           UUID PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    content      TEXT         NOT NULL,
    author_id    UUID         NOT NULL,
    answer_count INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_questions_author ON questions (author_id);
CREATE INDEX idx_questions_created_at ON questions (created_at);

CREATE TABLE answers (
    id          UUID PRIMARY KEY,
    question_id UUID NOT NULL,
    content     TEXT NOT NULL,
    author_id   UUID NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE
);
CREATE INDEX idx_answers_question ON answers (question_id);

-- ---------- 프로젝트 컨텍스트 (project-service) ----------
CREATE TABLE projects (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    owner_id    UUID         NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'RECRUITING',
    progress    TEXT,
    tags        TEXT,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_projects_owner ON projects (owner_id);

-- ---------- 랭킹 컨텍스트 (ranking-batch-worker write / ranking-api-service read) ----------
-- 주의: 'year' 는 H2/SQL 예약어라 사용 불가 → snapshot_year (skills/db/er-modeling: 예약어 회피)
CREATE TABLE ranking_snapshots (
    id            UUID PRIMARY KEY,
    snapshot_year INT  NOT NULL,
    week_of_year  INT  NOT NULL,
    entries       TEXT NOT NULL,
    generated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_ranking_year_week UNIQUE (snapshot_year, week_of_year)
);
