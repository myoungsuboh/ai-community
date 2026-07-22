<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { cardsApi } from '@/api/cards'
import { postsApi } from '@/api/posts'
import { rankingApi } from '@/api/ranking'

// 메인 대시보드 — 주간 랭킹(API-18) + 카드(API-04/02) + 게시글(API-01) 집계
const router = useRouter()

const ranking = useQuery({ queryKey: ['dash-ranking'], queryFn: () => rankingApi.weekly() })
const cards = useQuery({ queryKey: ['dash-cards'], queryFn: () => cardsApi.list({ sort: 'score', page: 0, size: 4 }) })
const posts = useQuery({ queryKey: ['dash-posts'], queryFn: () => postsApi.list({ page: 0, size: 5 }) })

const topRanking = computed(() => (ranking.data.value?.entries ?? []).slice(0, 3))
const topCards = computed(() => cards.data.value?.content ?? [])
const recentPosts = computed(() => posts.data.value?.content ?? [])
</script>

<template>
  <v-container class="container-max section">
    <p class="eyebrow text-primary mb-2">대시보드</p>
    <h1 class="display-xl mb-8">지금 커뮤니티에서</h1>

    <v-row>
      <!-- 주간 랭킹 -->
      <v-col cols="12" md="6">
        <v-card class="pa-6 h-100">
          <div class="d-flex justify-space-between align-center mb-4">
            <h2 class="display-md">🏆 주간 랭킹</h2>
            <v-btn size="small" variant="text" to="/rankings/weekly">전체</v-btn>
          </div>
          <p v-if="topRanking.length === 0" class="text-medium-emphasis">아직 이번 주 랭킹이 없어요.</p>
          <v-list v-else lines="one" density="comfortable">
            <v-list-item v-for="e in topRanking" :key="e.cardId" :title="e.title"
                         :subtitle="`${e.score}점`" @click="router.push(`/cards/${e.slug}`)">
              <template #prepend><span class="mr-2 font-display">{{ e.rank }}</span></template>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>

      <!-- 인기 카드 -->
      <v-col cols="12" md="6">
        <v-card class="pa-6 h-100">
          <div class="d-flex justify-space-between align-center mb-4">
            <h2 class="display-md">🔥 인기 카드</h2>
            <v-btn size="small" variant="text" to="/models">더보기</v-btn>
          </div>
          <p v-if="topCards.length === 0" class="text-medium-emphasis">카드가 아직 없어요.</p>
          <v-list v-else lines="two" density="comfortable">
            <v-list-item v-for="c in topCards" :key="c.cardId" :title="c.title"
                         :subtitle="c.category" @click="router.push(`/cards/${c.slug}`)" />
          </v-list>
        </v-card>
      </v-col>

      <!-- 최신 게시글 -->
      <v-col cols="12">
        <v-card class="pa-6">
          <div class="d-flex justify-space-between align-center mb-4">
            <h2 class="display-md">✍️ 최신 게시글</h2>
            <v-btn size="small" variant="text" to="/posts">전체</v-btn>
          </div>
          <p v-if="recentPosts.length === 0" class="text-medium-emphasis">게시글이 아직 없어요.</p>
          <v-list v-else lines="one" density="comfortable">
            <v-list-item v-for="p in recentPosts" :key="p.postId" :title="p.title"
                         @click="router.push(`/posts/${p.postId}`)" />
          </v-list>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
