---
name: "Spring Boot REST API 개발 표준 (MyBatis MVC)"
description: "Java Spring Boot + MyBatis 기반 MVC 구현 표준: Controller → Service → DAO → Mapper(XML) 레이어, 기능(도메인)별 디렉토리, 공통 ApiResponse 포맷, 전역 예외 처리, 자주 발생하는 타입 오류 해결책. REST API를 새로 만들거나 레이어·패키지 구조·응답 포맷·타입 캐스팅 오류를 다룰 때 읽는다. 키워드: @RestController, @GetMapping, @PostMapping, ResponseEntity, ApiResponse, Controller, Service, DAO, Mapper, MyBatis."
---

# Spring Boot REST API 개발 표준 (MyBatis MVC)

**ID:** `SKL-SPRING-BOOT-REST`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** Java Spring Boot + MyBatis 기반 MVC 구현 표준: Controller → Service → DAO → Mapper(XML) 레이어, 기능(도메인)별 디렉토리, 공통 ApiResponse 포맷, 전역 예외 처리, 자주 발생하는 타입 오류 해결책. REST API를 새로 만들거나 레이어·패키지 구조·응답 포맷·타입 캐스팅 오류를 다룰 때 읽는다. 키워드: @RestController, @GetMapping, @PostMapping, ResponseEntity, ApiResponse, Controller, Service, DAO, Mapper, MyBatis.

---

## 지시사항 (Instructions)

1. 레이어는 Controller → Service → DAO → Mapper(XML) 순서로 분리하고, 각 레이어는 바로 아래만 호출한다(건너뛰기 금지).
2. Controller는 요청 검증과 응답 변환만 담당하고 비즈니스 로직을 두지 않는다.
3. Service에 트랜잭션 경계(@Transactional)와 비즈니스 규칙을 둔다.
4. DB 접근은 MyBatis Mapper XML로 분리하고 동적 쿼리를 사용한다.
5. 응답은 공통 포맷 ApiResponse로 감싸 반환한다.

## 태그

`@RestController` `@GetMapping` `@PostMapping` `ResponseEntity` `ApiResponse` `Controller` `Service` `DAO` `Mapper` `MyBatis` `@PutMapping` `@DeleteMapping` `Repository` `spring-boot-rest` `backEnd` `ai-recommended`
