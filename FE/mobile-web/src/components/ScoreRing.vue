<script setup>
import { computed } from 'vue'

// 실전점수 도넛 링 (Card 불변식): 70+ 초록, 40~69 호박, 40 미만 회색
const props = defineProps({
  score: { type: Number, default: 0 },
  size: { type: Number, default: 64 },
})

const color = computed(() => {
  if (props.score >= 70) return 'rgb(var(--v-theme-success))'
  if (props.score >= 40) return 'rgb(var(--v-theme-warning))'
  return 'rgba(var(--v-theme-on-surface), 0.35)'
})

const R = 26
const C = 2 * Math.PI * R
const dash = computed(() => `${(Math.min(Math.max(props.score, 0), 100) / 100) * C} ${C}`)
</script>

<template>
  <div class="score-ring" :style="{ width: size + 'px', height: size + 'px' }">
    <svg viewBox="0 0 64 64" :width="size" :height="size">
      <circle cx="32" cy="32" :r="R" fill="none" stroke="rgba(var(--v-theme-on-surface),0.10)" stroke-width="6" />
      <circle
        cx="32" cy="32" :r="R" fill="none" :stroke="color" stroke-width="6" stroke-linecap="round"
        :stroke-dasharray="dash" transform="rotate(-90 32 32)"
      />
    </svg>
    <span class="score-ring__label font-display" :style="{ color }">{{ score }}</span>
  </div>
</template>

<style scoped>
.score-ring {
  position: relative;
  display: inline-grid;
  place-items: center;
}
.score-ring__label {
  position: absolute;
  font-weight: 800;
  font-size: 1rem;
}
</style>
