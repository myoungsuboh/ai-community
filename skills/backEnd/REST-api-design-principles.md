---
name: "API 설계 원칙 (REST/HTTP, 스택 중립)"
description: "REST/HTTP API 의 설계 원칙: 리소스 모델링·URL 명명, HTTP 메서드/상태코드 의미론, 일관된 에러 응답 포맷(RFC 7807), 필터·정렬·페이지네이션 쿼리 규약, 무상태성. 스택에 무관한 범용 표준으로, 새 엔드포인트를 설계하거나 응답·에러 포맷을 정할 때 읽는다. (버전 전략은 `api-versioning-swagger`, 멱등성은 `idempotency`, 페이지네이션 구현은 `pagination-filtering`, 인증/인가는 `authn-authz` 에 위임.) 키워드: REST, resource, HTTP method, status code, idempotent, RFC 7807, problem+json, content negotiation, HATEOAS."
---

# API 설계 원칙 (REST/HTTP, 스택 중립)

**ID:** `SKL-API-DESIGN-PRINCIPLES`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** REST/HTTP API 의 설계 원칙: 리소스 모델링·URL 명명, HTTP 메서드/상태코드 의미론, 일관된 에러 응답 포맷(RFC 7807), 필터·정렬·페이지네이션 쿼리 규약, 무상태성. 스택에 무관한 범용 표준으로, 새 엔드포인트를 설계하거나 응답·에러 포맷을 정할 때 읽는다. (버전 전략은 `api-versioning-swagger`, 멱등성은 `idempotency`, 페이지네이션 구현은 `pagination-filtering`, 인증/인가는 `authn-authz` 에 위임.) 키워드: REST, resource, HTTP method, status code, idempotent, RFC 7807, problem+json, content negotiation, HATEOAS.

---

## 지시사항 (Instructions)

1. 리소스 중심 URL: 경로는 동사가 아니라 명사(리소스)로 짓는다: /users/{id}/orders (O), /getUserOrders (X). 컬렉션은 복수형.
2. 메서드 의미 준수: GET(조회·부작용 없음)·POST(생성)·PUT(전체 교체)·PATCH(부분 수정)·DELETE(삭제). GET 으로 상태를 바꾸지 않는다.
3. 상태코드 의미 준수: 2xx 성공·4xx 클라이언트 잘못·5xx 서버 잘못. 200 에 에러를 담아 보내지 않는다(예: {success:false} 를 200 으로).
4. 일관된 에러 포맷: 모든 에러는 동일한 구조로 응답한다. RFC 7807(application/problem+json: type·title·status·detail) 또는 프로젝트 공통 에러 스키마를 따른다.
5. 조회 규약 표준화: 필터·정렬·페이지네이션을 쿼리 파라미터로 일관되게 노출한다(예: ?status=open&sort=-created_at&page=2).
6. 무상태성: 각 요청은 자기충족적이어야 한다: 서버 세션에 요청 간 상태를 숨기지 않는다(인증 토큰 등은 매 요청에 전달).

## 태그

`REST` `resource` `HTTP method` `status code` `idempotent` `RFC 7807` `problem+json` `content negotiation` `HATEOAS` `PATCH` `ETag` `api-design-principles` `backEnd` `ai-recommended`
