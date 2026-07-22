---
name: "CI/CD 파이프라인 표준"
description: "지속적 통합/배포(CI/CD) 파이프라인의 범용 표준: 단계 분리(린트→테스트→빌드→스캔→배포)와 앞 단계 실패 시 차단, 브랜치 보호 머지 게이트, 환경 분리·production 수동 승인, 불변 아티팩트 태깅·롤백, 시크릿 관리, 캐싱. 특정 CI 도구에 무관하다. 파이프라인을 만들거나 단계·승인·롤백을 정할 때 읽는다. 키워드: CI, CD, pipeline, build, deploy, branch protection, manual approval, artifact, rollback, GitHub Actions, GitLab CI."
---

# CI/CD 파이프라인 표준

**ID:** `SKL-CI-CD-PIPELINE`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 지속적 통합/배포(CI/CD) 파이프라인의 범용 표준: 단계 분리(린트→테스트→빌드→스캔→배포)와 앞 단계 실패 시 차단, 브랜치 보호 머지 게이트, 환경 분리·production 수동 승인, 불변 아티팩트 태깅·롤백, 시크릿 관리, 캐싱. 특정 CI 도구에 무관하다. 파이프라인을 만들거나 단계·승인·롤백을 정할 때 읽는다. 키워드: CI, CD, pipeline, build, deploy, branch protection, manual approval, artifact, rollback, GitHub Actions, GitLab CI.

---

## 지시사항 (Instructions)

1. 머지 게이트는 자동 검증으로: 모든 변경(PR/MR)은 CI를 통과해야만 통합 브랜치로 머지할 수 있게 브랜치 보호 규칙을 강제한다. CI만 돌리고 보호 규칙이 없으면 실패해도 머지되어 의미가 없다.
2. 단계를 분리하고 빠르게 실패(fail-fast): 파이프라인을 린트→테스트→빌드→보안 스캔→배포로 나누고, 앞 단계가 실패하면 후속 단계를 건너뛴다. 값싸고 빠른 검증(린트)을 앞에, 비싼 검증(빌드·배포)을 뒤에 둔다.
3. 환경을 분리하고 production은 수동 승인: dev·staging·production 배포를 분리한다. production 배포는 사람의 명시적 승인(manual approval)을 필수로 해, 검증 없이 장애가 전파되지 않게 한다.
4. 아티팩트는 한 번 빌드해 승격(build once, promote): 빌드 산출물은 콘텐츠/커밋 기반 고유 태그로 식별하고, 환경마다 다시 빌드하지 말고 같은 아티팩트를 승격한다. 어떤 버전이 배포됐는지 추적되고 이전 버전으로 롤백이 가능해진다.
5. 시크릿은 비밀 저장소에서, 로그에 노출 금지: 자격 증명은 CI 플랫폼의 시크릿 관리 기능(또는 외부 비밀 저장소)에 두고, 파이프라인 로그에 마스킹되는지 확인한다. 평문 환경변수·코드·로그에 시크릿을 노출하지 않는다.
6. 재현 가능하고 빠르게: 의존성은 잠금 파일(lock) 기반으로 결정적으로 설치하고, 캐싱으로 반복 비용을 줄인다. 파이프라인 정의는 코드로 버전 관리한다(pipeline as code).

## 태그

`CI` `CD` `pipeline` `build` `deploy` `branch protection` `manual approval` `artifact` `rollback` `GitHub Actions` `GitLab CI` `github-actions` `workflow` `.github/workflows` `on: push` `ci-cd-pipeline` `devops` `ai-recommended`
