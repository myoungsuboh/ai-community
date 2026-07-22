---
name: "페이지네이션 & 필터링 표준 (Pagination & Filtering)"
description: "커서 기반·오프셋 기반 페이지네이션과 동적 필터링 API 설계 표준(스택 중립). 목록 API를 만들거나 대용량 데이터 조회·정렬·필터를 설계할 때 읽는다. 키워드: pagination, cursor, offset, filtering, page, limit, sort."
---

# 페이지네이션 & 필터링 표준 (Pagination & Filtering)

**ID:** `SKL-PAGINATION-FILTERING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 커서 기반·오프셋 기반 페이지네이션과 동적 필터링 API 설계 표준(스택 중립). 목록 API를 만들거나 대용량 데이터 조회·정렬·필터를 설계할 때 읽는다. 키워드: pagination, cursor, offset, filtering, page, limit, sort.

---

## 지시사항 (Instructions)

1. 대용량 데이터셋은 오프셋 대신 커서 기반 페이지네이션을 사용해 OFFSET N 성능 저하를 방지한다.
2. 페이지네이션 응답에 next_cursor(또는 next_page), total_count, has_more를 포함한다.
3. 필터 파라미터는 명시적 허용목록(whitelist)으로만 처리하고, SQL/NoSQL 인젝션을 방지한다.
4. 기본 정렬 기준을 항상 명시하고(예: created_at DESC, id DESC), 정렬 없이 페이지네이션하지 않는다.
5. 페이지 크기(limit)에 최대값을 설정한다(예: 최대 100). 클라이언트가 무제한 요청하지 못하게 한다.

## 태그

`pagination` `cursor` `offset` `filtering` `page` `limit` `sort` `pagination-filtering` `backEnd` `ai-recommended`
