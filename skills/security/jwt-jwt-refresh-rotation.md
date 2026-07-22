---
name: "JWT Refresh Token 회전 + 재사용 탐지 (OAuth 2.1)"
description: "액세스+리프레시 토큰 구조에서 OAuth 2.1 권장 패턴인 회전(Rotation)·재사용 탐지(Reuse Detection)·패밀리 무효화(Family Revocation)를 구현하는 스택 무관 범용 표준. 모바일 앱·SPA·OAuth 흐름을 가진 서비스의 토큰 발급/갱신/로그아웃을 설계하거나, 토큰 저장 위치·TTL 정책을 정할 때 읽는다. 키워드: jwt, JsonWebToken, Bearer, refreshToken, Jwts.builder, Authorization, rotation, reuse detection, family revocation."
---

# JWT Refresh Token 회전 + 재사용 탐지 (OAuth 2.1)

**ID:** `SKL-JWT-REFRESH-ROTATION`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 액세스+리프레시 토큰 구조에서 OAuth 2.1 권장 패턴인 회전(Rotation)·재사용 탐지(Reuse Detection)·패밀리 무효화(Family Revocation)를 구현하는 스택 무관 범용 표준. 모바일 앱·SPA·OAuth 흐름을 가진 서비스의 토큰 발급/갱신/로그아웃을 설계하거나, 토큰 저장 위치·TTL 정책을 정할 때 읽는다. 키워드: jwt, JsonWebToken, Bearer, refreshToken, Jwts.builder, Authorization, rotation, reuse detection, family revocation.

---

## 지시사항 (Instructions)

1. 회전(Rotation): 리프레시 토큰은 사용할 때마다 새 토큰으로 교체하고, 이전 토큰은 즉시 폐기한다. 한 토큰은 단 1회만 사용 가능하다.
2. 재사용 탐지(Reuse Detection): 이미 폐기된 토큰이 다시 들어오면 도난 신호로 보고 즉시 차단한다.
3. 패밀리 무효화(Family Revocation): 같은 로그인 세션에서 파생된 토큰은 같은 패밀리(family)로 묶고, 재사용이 탐지되면 토큰 하나가 아니라 패밀리 전체를 무효화한다.
4. 저장은 해시로: 리프레시 토큰 원문이 아니라 해시(예: SHA-256)만 서버에 저장한다: DB가 유출돼도 토큰을 그대로 쓸 수 없다.
5. 저장 위치 분리: 액세스 토큰은 메모리에, 리프레시 토큰은 HttpOnly 쿠키 또는 OS 보안 저장소에 둔다. localStorage는 금지한다.
6. 짧은 TTL: 액세스 토큰은 짧게(15~30분), 리프레시 토큰은 적당히(7~14일) 두고, 미사용 토큰은 절대 만료 시각을 따로 둔다.
7. 로그아웃·비밀번호 변경 시 서버에서 폐기: 클라이언트 삭제만으로는 부족하다. 서버에서 해당 패밀리(또는 사용자 전체)를 즉시 폐기한다.

## 태그

`jwt` `JsonWebToken` `Bearer` `refreshToken` `Jwts.builder` `Authorization` `rotation` `reuse detection` `family revocation` `refresh-token` `token-rotation` `reuse-detection` `oauth2.1` `jwt-refresh-rotation` `security` `ai-recommended`
