---
name: "Flyway DB 마이그레이션 운영 표준"
description: "Flyway 기반 DB 형상 관리 표준. 디렉토리/네이밍/체크섬 규칙, 환경별 정책, Zero-downtime 마이그레이션, 보상 마이그레이션 롤백, Spring Boot/다중 모듈 통합, CI 검증을 다룬다. 스키마를 변경하거나 마이그레이션 파일을 작성·리뷰·배포할 때 읽는다. 키워드: flyway, migration, V1__, baseline, db.migration, zero-downtime, flyway_schema_history."
---

# Flyway DB 마이그레이션 운영 표준

**ID:** `SKL-DB-MIGRATION-FLYWAY`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** Flyway 기반 DB 형상 관리 표준. 디렉토리/네이밍/체크섬 규칙, 환경별 정책, Zero-downtime 마이그레이션, 보상 마이그레이션 롤백, Spring Boot/다중 모듈 통합, CI 검증을 다룬다. 스키마를 변경하거나 마이그레이션 파일을 작성·리뷰·배포할 때 읽는다. 키워드: flyway, migration, V1__, baseline, db.migration, zero-downtime, flyway_schema_history.

---

## 지시사항 (Instructions)

1. DB 형상 관리는 Flyway로 하고 V{버전}__{설명}.sql 네이밍을 따른다. SQL을 사람이 운영 DB에 직접 실행하지 않는다.
2. 이미 적용된 V 파일은 절대 수정하지 않고 새 버전을 추가한다(체크섬 불일치 방지).
3. DROP과 배포를 같은 릴리즈에서 동시에 하지 않는다.
4. flyway_schema_history 테이블을 수동으로 건드리지 않는다(flyway repair만 사용).
5. 컬럼 추가·삭제는 Zero-downtime을 위해 단계적으로 배포한다.
6. Flyway는 자동 롤백이 없으므로 보상 마이그레이션으로 되돌린다.

## 태그

`flyway` `migration` `V1__` `baseline` `db.migration` `zero-downtime` `flyway_schema_history` `V2__` `FlywayMigration` `db-migration-flyway` `db` `ai-recommended`
