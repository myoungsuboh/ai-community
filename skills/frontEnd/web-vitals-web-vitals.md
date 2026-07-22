---
name: "웹 바이탈 & 성능 예산 (Web Vitals)"
description: "Core Web Vitals(LCP·INP·CLS) 측정·목표·성능 예산·모니터링 표준. 성능 기준(릴리스 게이트)을 정하거나 LCP/INP/CLS를 개선·회귀 감지할 때 읽는다. 키워드: web-vitals, LCP, INP, CLS, Lighthouse CI, performance budget, RUM, P75."
---

# 웹 바이탈 & 성능 예산 (Web Vitals)

**ID:** `SKL-WEB-VITALS`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** Core Web Vitals(LCP·INP·CLS) 측정·목표·성능 예산·모니터링 표준. 성능 기준(릴리스 게이트)을 정하거나 LCP/INP/CLS를 개선·회귀 감지할 때 읽는다. 키워드: web-vitals, LCP, INP, CLS, Lighthouse CI, performance budget, RUM, P75.

---

## 지시사항 (Instructions)

1. LCP ≤ 2.5s, INP ≤ 200ms, CLS ≤ 0.1을 릴리스 기준으로 삼는다.
2. 번들 예산(예: 초기 JS ≤ 200kB gzip)을 정하고 CI에서 초과 시 경고한다.
3. 렌더 블로킹 리소스(동기 CSS·JS)를 최소화한다.
4. web-vitals/RUM으로 실사용자 데이터를 P75 기준으로 모니터링한다.
5. Lighthouse CI를 파이프라인에 넣어 회귀를 자동 감지한다.

## 태그

`web-vitals` `LCP` `INP` `CLS` `Lighthouse CI` `performance budget` `RUM` `P75` `lighthouse` `performance` `bundle` `rum` `frontEnd` `ai-recommended`
