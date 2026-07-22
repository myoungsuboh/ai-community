---
name: "개인정보 보호 & PII 관리 (GDPR/PIPA)"
description: "개인정보(PII)를 GDPR·PIPA에 맞게 수집·저장·처리·삭제하는 범용 표준. 수집 최소화, 비밀번호 단방향 해시, 민감정보 암호화, 로그 마스킹, 삭제권 이행을 다룬다. 사용자 정보를 다루거나 비밀번호·민감정보 저장·로그·삭제 요청 처리를 정할 때 읽는다. 키워드: pii, gdpr, pipa, 개인정보, password-hash, bcrypt, argon2, masking, 삭제권, data-retention."
---

# 개인정보 보호 & PII 관리 (GDPR/PIPA)

**ID:** `SKL-PRIVACY-PII`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 개인정보(PII)를 GDPR·PIPA에 맞게 수집·저장·처리·삭제하는 범용 표준. 수집 최소화, 비밀번호 단방향 해시, 민감정보 암호화, 로그 마스킹, 삭제권 이행을 다룬다. 사용자 정보를 다루거나 비밀번호·민감정보 저장·로그·삭제 요청 처리를 정할 때 읽는다. 키워드: pii, gdpr, pipa, 개인정보, password-hash, bcrypt, argon2, masking, 삭제권, data-retention.

---

## 지시사항 (Instructions)

1. 수집은 적게, 보관은 짧게: 꼭 필요한 최소한만 고지한 목적 안에서 수집하고('혹시 몰라' 금지), 항목별 보존 기간이 지나면 자동 폐기한다.
2. 저장은 복원 불가능하게: 비밀번호는 단방향 해시로만, 민감정보는 암호화 + 최소 권한으로 둔다. 유출돼도 그 자체로는 쓸모없는 형태가 원칙이다.
3. 처리는 증빙 가능하게: 동의·삭제 등 처리 행위를 감사 로그로 남기고, 정보주체의 삭제 요청은 흩어진 모든 사본까지 기한 내 이행한다.

## 태그

`pii` `gdpr` `pipa` `개인정보` `password-hash` `bcrypt` `argon2` `masking` `삭제권` `data-retention` `PII` `GDPR` `encrypt` `mask` `anonymize` `consent` `personal_data` `privacy-pii` `security` `ai-recommended`
