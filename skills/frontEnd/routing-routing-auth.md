---
name: "라우팅 인증/권한 표준 (Routing Auth & Authorization)"
description: "클라이언트 라우팅에서 인증·권한을 다루는 범용 표준: 라우트 가드 일괄 검사, 공개 경로 단일 관리, 토큰 영속 저장 지양, 접근 로그 비동기, 클라이언트 권한 불신·서버 재검증. 특정 프레임워크/라우터에 무관하다. 라우트를 추가하거나 인증/권한 가드·토큰 처리를 구현·정비할 때 읽는다. 키워드: routing, route guard, auth, authorization, RBAC, public routes, token storage, server-side authorization."
---

# 라우팅 인증/권한 표준 (Routing Auth & Authorization)

**ID:** `SKL-ROUTING-AUTH`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 클라이언트 라우팅에서 인증·권한을 다루는 범용 표준: 라우트 가드 일괄 검사, 공개 경로 단일 관리, 토큰 영속 저장 지양, 접근 로그 비동기, 클라이언트 권한 불신·서버 재검증. 특정 프레임워크/라우터에 무관하다. 라우트를 추가하거나 인증/권한 가드·토큰 처리를 구현·정비할 때 읽는다. 키워드: routing, route guard, auth, authorization, RBAC, public routes, token storage, server-side authorization.

---

## 지시사항 (Instructions)

1. 인증·권한은 라우트 가드에서 일괄 처리: 화면 진입 전 한 곳(전역 네비게이션 가드)에서 인증 여부 → 인증 필요 라우트 → 역할/권한 순으로 검사한다. 화면마다 즉석 검사를 흩뿌리지 않는다.
2. 공개 경로는 단일 출처로 관리: 인증이 필요 없는 경로(로그인·약관·오류 등)는 한 곳에서 정의하고 가드가 그것만 참조한다. 같은 정보를 가드와 화면 메타에 이중으로 두지 않는다.
3. 접근 토큰은 영속 저장소에 두지 않는다: 접근 토큰(access token)은 localStorage 등 영속 저장소·디스크에 저장하지 않고 메모리에만 둔다. 단, 메모리 보관은 XSS *노출면*을 줄일 뿐 안전을 보장하지 않으므로(런타임 토큰도 탈취 가능), 짧은 만료·httpOnly 쿠키 옵션을 함께 고려한다. 재인증은 서버측 비밀(예: httpOnly 쿠키의 갱신 토큰)으로 재발급받는다.
4. 접근 로그는 비동기로(fire-and-forget): 화면 전환 성공 후 접근 로그를 비동기로 보내고 실패는 무시한다. 로그 전송이 화면 전환을 막거나 느리게 하지 않는다.
5. 클라이언트 권한 판단은 신뢰하지 않는다: 가드·화면의 권한 분기는 UX(메뉴·버튼 노출)용일 뿐이다. 실제 강제는 서버가 모든 요청에서 다시 검증한다. 클라이언트는 우회 가능하다고 가정한다.
6. 권한 분기는 라우트와 컴포넌트 양쪽에: 라우트 가드만 막으면 화면 내부 위험 동작이 그대로 노출되고, 컴포넌트만 막으면 URL 직접 입력으로 우회된다. 둘 다 둔다(그리고 최종 강제는 서버).

## 태그

`routing` `route guard` `auth` `authorization` `RBAC` `public routes` `token storage` `server-side authorization` `vue-router` `router.push` `router-link` `beforeEach` `useRouter` `createRouter` `meta.requiresAuth` `routing-auth` `frontEnd` `ai-recommended`
