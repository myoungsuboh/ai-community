-- V2: 댓글 논리 삭제 컬럼 (skills/db/soft-delete-soft-delete-audit.md)
-- NULL = 활성. 조회는 기본적으로 deleted_at IS NULL 만 노출한다.
ALTER TABLE comments ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE;
CREATE INDEX idx_comments_active ON comments (card_id, deleted_at);
