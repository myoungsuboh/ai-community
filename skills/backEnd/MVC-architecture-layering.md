---
name: "애플리케이션 아키텍처: 레이어링 & 관심사 분리 (MVC/Clean)"
description: "컨트롤러·서비스·리포지토리 레이어를 분리하고 비즈니스 로직의 위치와 의존성 방향을 규정하는 스택 중립 아키텍처 가이드. 새 기능의 코드 배치를 정하거나, 컨트롤러·UI에 로직이 섞이지 않게 검토할 때 읽는다. 키워드: MVC, MVVM, layered-architecture, clean-architecture, hexagonal, controller, service, repository, separation-of-concerns, dependency-inversion, DTO."
---

# 애플리케이션 아키텍처: 레이어링 & 관심사 분리 (MVC/Clean)

**ID:** `SKL-ARCHITECTURE-LAYERING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 컨트롤러·서비스·리포지토리 레이어를 분리하고 비즈니스 로직의 위치와 의존성 방향을 규정하는 스택 중립 아키텍처 가이드. 새 기능의 코드 배치를 정하거나, 컨트롤러·UI에 로직이 섞이지 않게 검토할 때 읽는다. 키워드: MVC, MVVM, layered-architecture, clean-architecture, hexagonal, controller, service, repository, separation-of-concerns, dependency-inversion, DTO.

---

## 지시사항 (Instructions)

1. 레이어를 분리한다: Controller(요청/응답)·Service(비즈니스 로직)·Repository(데이터 접근). 한 파일이 두 책임을 겸하지 않는다.
2. 비즈니스 로직은 Service 레이어에만 둔다: 컨트롤러는 입력 검증·호출·응답 변환만, UI/뷰는 표현만 담당한다.
3. 의존성은 안쪽(도메인)으로만 향한다: Controller→Service→Repository. 역방향 참조(Repository가 Service 호출)를 금지한다.
4. 레이어 간 경계는 DTO로 주고받고, DB 엔티티를 컨트롤러/뷰까지 그대로 노출하지 않는다.
5. 외부 의존성(DB·외부 API·파일)은 인터페이스 뒤로 추상화해 도메인 로직이 구현에 묶이지 않게 한다 (의존성 역전).
6. 프론트엔드는 MVVM/컴포넌트 구조로: 뷰(템플릿)·상태(store/composable)·API 호출(service)을 분리하고 컴포넌트에 비즈니스 로직을 두지 않는다.

## 태그

`MVC` `MVVM` `layered-architecture` `clean-architecture` `hexagonal` `controller` `service` `repository` `separation-of-concerns` `dependency-inversion` `DTO` `architecture-layering` `backEnd` `ai-recommended`
