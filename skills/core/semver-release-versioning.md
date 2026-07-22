---
name: "릴리스 & 버전 관리 (Release & Versioning)"
description: "소프트웨어 릴리스의 버전 부여(SemVer)·변경 이력(CHANGELOG)·Git 태그·릴리스 노트·폐기(deprecation) 정책 표준. 스택에 무관한 범용 표준으로, 버전을 올리거나 릴리스를 내거나 변경 이력을 정리할 때 읽는다. (REST API 계약 버전은 `api-versioning-swagger`, 의존성 버전 위생은 `dependency-management` 에 위임.) 키워드: semver, MAJOR.MINOR.PATCH, CHANGELOG, git tag, release notes, breaking change, deprecation, conventional commits."
---

# 릴리스 & 버전 관리 (Release & Versioning)

**ID:** `SKL-RELEASE-VERSIONING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 소프트웨어 릴리스의 버전 부여(SemVer)·변경 이력(CHANGELOG)·Git 태그·릴리스 노트·폐기(deprecation) 정책 표준. 스택에 무관한 범용 표준으로, 버전을 올리거나 릴리스를 내거나 변경 이력을 정리할 때 읽는다. (REST API 계약 버전은 `api-versioning-swagger`, 의존성 버전 위생은 `dependency-management` 에 위임.) 키워드: semver, MAJOR.MINOR.PATCH, CHANGELOG, git tag, release notes, breaking change, deprecation, conventional commits.

---

## 지시사항 (Instructions)

1. SemVer 준수: 버전을 MAJOR.MINOR.PATCH 로 부여한다. 호환이 깨지면 MAJOR, 하위호환 기능추가는 MINOR, 하위호환 버그수정은 PATCH 를 올린다.
2. 변경 이력 유지: 모든 릴리스는 CHANGELOG 에 사용자 영향 중심(Added/Changed/Fixed/Removed/Deprecated/Security)으로 기록한다. 커밋 해시 나열이 아니라 '무엇이 바뀌었나'를 쓴다.
3. 불변 태그: 릴리스마다 annotated Git 태그(vMAJOR.MINOR.PATCH)를 찍고, 이미 배포된 태그는 절대 옮기거나 재사용하지 않는다.
4. Breaking change 명시: 호환이 깨지는 변경은 릴리스 노트·CHANGELOG 상단에 명확히 표시하고 마이그레이션 방법을 함께 적는다.
5. 단계적 폐기: 기능을 즉시 제거하지 않고 deprecated 고지 → 유예 기간 → 다음 MAJOR 에서 제거 순으로 진행한다.
6. 0.x 주의: 1.0.0 이전(0.y.z)은 공개 API 가 불안정함을 뜻한다. 안정성을 약속하려면 1.0.0 을 끊는다.

## 태그

`semver` `MAJOR.MINOR.PATCH` `CHANGELOG` `git tag` `release notes` `breaking change` `deprecation` `conventional commits` `Keep a Changelog` `release-versioning` `core` `ai-recommended`
