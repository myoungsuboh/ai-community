---
name: "입력값 검증 표준 (Input Validation)"
description: "서버측 입력값 검증의 범용 표준으로, 입력 신뢰 금지·진입점 일괄 검증·선언적 스키마·계층별(진입점·도메인) 방어·일관된 오류 응답을 다룬다(스택 무관). 요청/입력 검증을 추가·정비하거나 검증 오류 응답 포맷을 통일할 때, Create/Update 등 상황별 제약을 나눌 때 읽는다. 키워드: validation, server-side, schema, error response, fail-fast, 검증 그룹, 커스텀 검증."
---

# 입력값 검증 표준 (Input Validation)

**ID:** `SKL-VALIDATION-BEAN`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 서버측 입력값 검증의 범용 표준으로, 입력 신뢰 금지·진입점 일괄 검증·선언적 스키마·계층별(진입점·도메인) 방어·일관된 오류 응답을 다룬다(스택 무관). 요청/입력 검증을 추가·정비하거나 검증 오류 응답 포맷을 통일할 때, Create/Update 등 상황별 제약을 나눌 때 읽는다. 키워드: validation, server-side, schema, error response, fail-fast, 검증 그룹, 커스텀 검증.

---

## 지시사항 (Instructions)

1. 입력은 신뢰하지 않는다: 클라이언트·외부 시스템에서 온 모든 값은 잠재적으로 잘못됐거나 악의적이라고 가정한다. 클라이언트측 검증은 UX 보조일 뿐, 보안·정합성의 근거가 아니다.
2. 검증은 서버측에서: 실제 강제는 반드시 서버(신뢰 경계 안쪽)에서 한다. 검증의 책임을 클라이언트에 떠넘기지 않는다.
3. 진입점에서 빠르게 실패(fail-fast): 입력은 시스템에 들어오는 진입점에서 즉시 검증해, 잘못된 값이 도메인 로직 깊숙이 흘러들지 않게 한다.
4. 선언적 스키마로 표현: 제약(필수·길이·범위·형식)을 코드 흐름에 흩뿌리지 말고, 입력 모델에 선언적으로 붙여 한곳에서 읽히게 한다.
5. 계층별 방어: 진입점 검증을 기본으로 하되, 입력이 진입점을 거치지 않을 수 있는 경로(내부 호출·배치·스케줄러)를 위해 도메인 불변식은 도메인 계층에서도 한 번 더 지킨다.
6. 일관된 오류 응답: 검증 실패는 '클라이언트 잘못'임을 분명히 하는 형식(예: 4xx 계열 + 어떤 필드가 왜 틀렸는지)으로, 모든 진입점에서 동일한 포맷으로 돌려준다. 내부 구현 정보는 노출하지 않는다.
7. 다국어·메시지 분리: 오류 메시지는 하드코딩하지 말고 메시지 카탈로그로 분리해 다국어와 문구 변경에 대응한다.

## 태그

`validation` `server-side` `schema` `error response` `fail-fast` `검증 그룹` `커스텀 검증` `@Valid` `@Validated` `@NotNull` `@NotBlank` `@Size` `@Pattern` `@Email` `@Min` `@Max` `ConstraintValidator` `BindingResult` `validation-bean` `backEnd` `ai-recommended`
