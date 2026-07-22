---
name: "컨테이너화 표준 (Containerization)"
description: "컨테이너 이미지의 범용 표준(작은 이미지, 멀티스테이지 빌드, non-root 실행, 레이어 캐시, 빌드 컨텍스트 제외, 불변 태그, 런타임 시크릿 주입, 취약점 스캔): 런타임/도구에 무관한 OCI 보편 개념. 서비스를 컨테이너화하거나 이미지 크기·공격 표면·시크릿 주입을 정할 때 읽는다. 키워드: container, OCI, image, multi-stage, non-root, dockerignore, vulnerability scan, Dockerfile, docker-compose."
---

# 컨테이너화 표준 (Containerization)

**ID:** `SKL-DOCKER-CONTAINERIZATION`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 컨테이너 이미지의 범용 표준(작은 이미지, 멀티스테이지 빌드, non-root 실행, 레이어 캐시, 빌드 컨텍스트 제외, 불변 태그, 런타임 시크릿 주입, 취약점 스캔): 런타임/도구에 무관한 OCI 보편 개념. 서비스를 컨테이너화하거나 이미지 크기·공격 표면·시크릿 주입을 정할 때 읽는다. 키워드: container, OCI, image, multi-stage, non-root, dockerignore, vulnerability scan, Dockerfile, docker-compose.

---

## 지시사항 (Instructions)

1. 이미지는 작게: 최종 이미지에는 애플리케이션을 실행하는 데 꼭 필요한 것만 남긴다. 빌드 도구·소스코드·캐시·문서는 런타임 이미지에서 제거해 크기와 공격 표면을 줄인다.
2. 빌드와 런타임을 분리(멀티스테이지): 빌드 단계와 실행 단계를 나눠, 컴파일러·패키지 매니저·중간 산출물이 최종 이미지로 새지 않게 한다.
3. non-root로 실행: 컨테이너 프로세스는 root가 아닌 전용 비권한 사용자로 돌린다. 컨테이너 탈출·권한 상승의 피해 범위를 줄인다.
4. 레이어 캐시를 의도적으로 활용: 자주 안 바뀌는 것(의존성 매니페스트)을 먼저, 자주 바뀌는 것(소스코드)을 나중에 복사해 캐시 적중률을 높인다. 한 번에 모든 걸 복사하면 코드 한 줄 바꿔도 의존성을 다시 받는다.
5. 빌드 컨텍스트를 줄인다: 무시 파일(.dockerignore 등)로 VCS 메타데이터·의존성 디렉터리·시크릿·테스트 파일을 빌드 컨텍스트에서 제외한다. 빌드가 빨라지고 민감 파일이 이미지에 섞이지 않는다.
6. 태그는 불변(immutable)하게: latest 같은 떠다니는 태그 대신 구체적 버전 또는 다이제스트(SHA)로 고정해 재현 가능한 빌드를 보장한다.
7. 시크릿은 런타임에 주입: API 키·비밀번호를 이미지 레이어(빌드 인자/환경변수)에 굽지 않는다. 런타임 환경변수·시크릿 마운트로 외부에서 주입한다: 이미지 레이어는 누구나 들여다볼 수 있다.
8. 이미지는 불변·일회용으로: 실행 중 컨테이너를 고쳐 상태를 쌓지 않는다. 상태는 외부(볼륨·DB)에 두고, 이미지는 언제 버리고 다시 띄워도 같게 동작하게 한다.
9. 취약점을 스캔한다: 베이스 이미지와 의존성의 알려진 취약점(CVE)을 빌드 파이프라인에서 스캔하고, 베이스 이미지를 주기적으로 갱신한다.

## 태그

`container` `OCI` `image` `multi-stage` `non-root` `dockerignore` `vulnerability scan` `Dockerfile` `docker-compose` `COPY` `RUN` `FROM` `docker build` `docker-containerization` `devops` `ai-recommended`
