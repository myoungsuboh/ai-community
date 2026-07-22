package com.aicommunity.useractivity.support;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * cards 테이블의 비정규화 카운터(좋아요/북마크/댓글 수) 갱신.
 * cards 는 curation 이 소유하지만 공유 DB 이므로 활동 반영 시 여기서 증감한다.
 * 컬럼명은 화이트리스트 메서드로만 노출(인젝션 방지). 카운터는 0 미만으로 내려가지 않게 보정.
 */
@Component
public class CardCounterUpdater {

    private final JdbcTemplate jdbc;

    public CardCounterUpdater(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void likes(UUID cardId, int delta) {
        adjust("like_count", cardId, delta);
    }

    public void bookmarks(UUID cardId, int delta) {
        adjust("bookmark_count", cardId, delta);
    }

    public void comments(UUID cardId, int delta) {
        adjust("comment_count", cardId, delta);
    }

    private void adjust(String column, UUID cardId, int delta) {
        // column 은 내부 상수만 전달됨(외부 입력 아님)
        jdbc.update("UPDATE cards SET " + column + " = GREATEST(" + column + " + ?, 0) WHERE id = ?",
                delta, cardId);
    }
}
