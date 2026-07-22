<script setup>
import { computed } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { cardsApi } from '@/api/cards'
import CardTile from '@/components/CardTile.vue'

// AI 모델/코드 공유 — 발행된 카드 전체를 최신순으로 (API-04: GET /cards)
const { data, isLoading, isError, refetch } = useQuery({
  queryKey: ['cards', 'models'],
  queryFn: () => cardsApi.list({ sort: 'recent', page: 0, size: 24 }),
})
const cards = computed(() => data.value?.content ?? [])
</script>

<template>
  <v-container class="container-max section">
    <p class="eyebrow text-primary mb-2">모아보기</p>
    <h1 class="display-xl mb-2">AI 모델 · 코드 공유</h1>
    <p class="lead text-medium-emphasis mb-8">커뮤니티가 큐레이션한 AI 레포·스킬을 최신순으로.</p>

    <div v-if="isLoading">
      <v-row><v-col v-for="n in 6" :key="n" cols="12" sm="6" lg="4"><v-skeleton-loader type="card" /></v-col></v-row>
    </div>
    <v-alert v-else-if="isError" type="error" variant="tonal">
      불러오지 못했어요.
      <template #append><v-btn size="small" variant="text" @click="refetch()">다시 시도</v-btn></template>
    </v-alert>
    <div v-else-if="cards.length === 0" class="text-center py-16">
      <p class="display-md mb-2">아직 공유된 모델이 없어요.</p>
      <v-btn class="mt-3" color="primary" variant="flat" to="/submit">제보하기</v-btn>
    </div>
    <v-row v-else>
      <v-col v-for="card in cards" :key="card.cardId" cols="12" sm="6" lg="4"><CardTile :card="card" /></v-col>
    </v-row>
  </v-container>
</template>
