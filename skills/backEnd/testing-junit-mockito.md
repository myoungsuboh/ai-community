---
name: "백엔드 테스트 표준 (Backend Testing)"
description: "백엔드 자동화 테스트의 스택 적용 표준: 슬라이스(계층) 테스트 경계와 JUnit5·Mockito·Testcontainers 구현 예시를 다룬다. 범용 개념(피라미드·AAA·격리·모킹·Clock·비결정성)은 unit-testing·integration-testing·test-strategy 에 위임한다. 테스트를 새로 짜거나 느린·flaky 테스트를 정비할 때 읽는다. 키워드: 슬라이스 테스트, 단위 테스트, 통합 테스트, junit, mockito, testcontainers, @WebMvcTest, @MybatisTest, MockMvc, Clock, flaky."
---

# 백엔드 테스트 표준 (Backend Testing)

**ID:** `SKL-TESTING-JUNIT-MOCKITO`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 백엔드 자동화 테스트의 스택 적용 표준: 슬라이스(계층) 테스트 경계와 JUnit5·Mockito·Testcontainers 구현 예시를 다룬다. 범용 개념(피라미드·AAA·격리·모킹·Clock·비결정성)은 unit-testing·integration-testing·test-strategy 에 위임한다. 테스트를 새로 짜거나 느린·flaky 테스트를 정비할 때 읽는다. 키워드: 슬라이스 테스트, 단위 테스트, 통합 테스트, junit, mockito, testcontainers, @WebMvcTest, @MybatisTest, MockMvc, Clock, flaky.

---

## 지시사항 (Instructions)

1. 테스트 피라미드에 따라 단위 테스트를 다수, 통합 테스트를 소수로 작성한다
2. 서비스 단위 테스트는 Mockito given/when/then 으로 의존성을 모킹한다
3. 컨트롤러는 @WebMvcTest 와 MockMvc 로 슬라이스 테스트한다
4. 통합 테스트는 Testcontainers 로 실제 DB·Kafka·Redis 를 격리 실행한다
5. 시간 의존 코드는 Clock 을 주입해 결정적으로 테스트한다

## 태그

`슬라이스 테스트` `단위 테스트` `통합 테스트` `junit` `mockito` `testcontainers` `@WebMvcTest` `@MybatisTest` `MockMvc` `Clock` `flaky` `@Test` `@Mock` `@InjectMocks` `when(` `verify(` `assertEquals` `Mockito` `@SpringBootTest` `testing-junit-mockito` `backEnd` `ai-recommended`
