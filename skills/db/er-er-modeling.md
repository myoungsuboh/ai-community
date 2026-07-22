---
name: "ER 모델링 컨벤션"
description: "엔티티-관계 모델링, 정규화, 네이밍, 관계 설계 표준(DB 중립). 새 테이블·스키마를 설계하거나 PK·FK·N:M 관계·정규화 수준을 정할 때 읽는다. 키워드: er, entity, relationship, normalization, foreign-key, schema, naming, uuid."
---

# ER 모델링 컨벤션

**ID:** `SKL-ER-MODELING`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** 엔티티-관계 모델링, 정규화, 네이밍, 관계 설계 표준(DB 중립). 새 테이블·스키마를 설계하거나 PK·FK·N:M 관계·정규화 수준을 정할 때 읽는다. 키워드: er, entity, relationship, normalization, foreign-key, schema, naming, uuid.

---

## 지시사항 (Instructions)

1. 테이블·컬럼명은 snake_case, 복수형(users·orders)을 사용하고, 예약어와 충돌하는 이름을 피한다.
2. 기본 키는 UUID(v7) 또는 auto-increment BIGINT를 사용하고, 비즈니스 키(이메일·주문번호)를 PK로 사용하지 않는다.
3. 제3정규형(3NF)을 기본으로 설계하되, 성능이 필요하면 의도적으로 비정규화하고 이유를 주석으로 남긴다.
4. FK 제약은 반드시 선언해 참조 무결성을 DB가 보장하게 한다: 애플리케이션에서만 검증하지 않는다.
5. N:M 관계는 중간 테이블(join table)로 해소하고, 중간 테이블에도 감사 컬럼(created_at)을 포함한다.

## 태그

`er` `entity` `relationship` `normalization` `foreign-key` `schema` `naming` `uuid` `er-modeling` `db` `ai-recommended`
