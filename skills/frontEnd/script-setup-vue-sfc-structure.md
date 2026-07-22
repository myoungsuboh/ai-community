---
name: "Vue SFC 파일 구조 및 작성 규칙 (Vue SFC Structure)"
description: "Vue 3 단일 파일 컴포넌트(.vue)의 블록 순서, 함수 작성 방식, 스크립트 내부 정렬 표준입니다. 모든 .vue 파일이 동일한 골격으로 읽히도록 강제하며 신규 작성·리팩토링 시 읽습니다. 키워드 SFC, script setup, arrow function, 라이프사이클, TDZ, 정렬 순서."
---

# Vue SFC 파일 구조 및 작성 규칙 (Vue SFC Structure)

**ID:** `SKL-VUE-SFC-STRUCTURE`  
**범위(Scope):** AI Recommended  
**우선순위:** High  
**적용 조건:** Vue 3 단일 파일 컴포넌트(.vue)의 블록 순서, 함수 작성 방식, 스크립트 내부 정렬 표준입니다. 모든 .vue 파일이 동일한 골격으로 읽히도록 강제하며 신규 작성·리팩토링 시 읽습니다. 키워드 SFC, script setup, arrow function, 라이프사이클, TDZ, 정렬 순서.

---

## 지시사항 (Instructions)

1. 블록 순서는 <script setup> → <template> → <style> 로 고정한다.
2. 함수는 function 키워드 대신 arrow function(const name = (args) => {})으로 선언한다.
3. 라이프사이클 훅은 <script setup>의 가장 마지막에 모은다.
4. script 내부는 imports → props/emits → state → 헬퍼 → computed → watch → defineExpose → 라이프사이클 순서로 정렬한다.
5. const arrow function은 호이스팅되지 않으므로 즉시 실행되는 곳에서 호출되는 헬퍼는 선언이 먼저 와야 한다(TDZ 주의).

## 태그

`<script setup>` `<template>` `<style scoped>` `defineProps` `defineEmits` `defineExpose` `vue-sfc-structure` `frontEnd` `ai-recommended`
