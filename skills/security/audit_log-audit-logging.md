---
name: "보안 감사 로깅 & 이벤트 추적"
description: "보안 관련 이벤트(인증·인가·데이터 변경·관리 행위)를 변경 불가 감사 로그로 기록하고 이상 징후를 탐지하는 표준. 인증/인가 흐름을 만들거나 민감 데이터 변경·관리 기능을 추가할 때, 감사 로그 구조·보존·알람을 정할 때 읽는다. 키워드: audit_log, audit, event_log, security_event, append-only, structured_log, SIEM."
---

# 보안 감사 로깅 & 이벤트 추적

**ID:** `SKL-AUDIT-LOGGING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 보안 관련 이벤트(인증·인가·데이터 변경·관리 행위)를 변경 불가 감사 로그로 기록하고 이상 징후를 탐지하는 표준. 인증/인가 흐름을 만들거나 민감 데이터 변경·관리 기능을 추가할 때, 감사 로그 구조·보존·알람을 정할 때 읽는다. 키워드: audit_log, audit, event_log, security_event, append-only, structured_log, SIEM.

---

## 지시사항 (Instructions)

1. 인증 이벤트(로그인 성공·실패·로그아웃·토큰 갱신)와 인가 실패는 반드시 감사 로그에 기록한다.
2. 감사 로그는 변경 불가(append-only)로 저장하고, 애플리케이션 로그와 분리된 스토리지에 보관한다.
3. 감사 로그 항목에는 타임스탬프(UTC)·행위자(user_id·IP)·대상(resource_id)·행위·결과를 포함한다.
4. 감사 로그에 개인정보를 노출하지 않고, 민감 값은 마스킹하거나 해시로 참조한다.
5. 로그인 실패 반복·비정상 시간대 접근·대용량 데이터 조회 등 이상 패턴에 알람을 설정한다.

## 태그

`audit_log` `audit` `event_log` `security_event` `append-only` `structured_log` `SIEM` `audit-logging` `security` `ai-recommended`
