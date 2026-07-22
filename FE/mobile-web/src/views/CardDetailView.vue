<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { cardsApi } from '@/api/cards'
import ScoreRing from '@/components/ScoreRing.vue'

const route = useRoute()
const router = useRouter()
const slug = computed(() => route.params.cardSlug)

const { data: card, isLoading, isError, error } = useQuery({
  queryKey: computed(() => ['card', slug.value]),
  queryFn: () => cardsApi.detail(slug.value),
  retry: false,
})

const notFound = computed(() => error.value?.response?.status === 404)

const axes = computed(() => {
  const c = card.value
  if (!c) return []
  return [
    { label: '문서화', value: c.scoreAxisDocs },
    { label: '활성도', value: c.scoreAxisActivity },
    { label: '인기도', value: c.scoreAxisPopularity },
    { label: '유지보수', value: c.scoreAxisMaintenance },
  ]
})
const summaries = computed(() =>
  [card.value?.summaryLine1, card.value?.summaryLine2, card.value?.summaryLine3].filter(Boolean),
)
</script>

<template>
  <v-container class="container-max section">
    <div v-if="isLoading">
      <v-skeleton-loader type="article, list-item-three-line" />
    </div>

    <div v-else-if="notFound" class="text-center py-16">
      <h1 class="display-lg mb-3">카드를 찾을 수 없어요</h1>
      <p class="text-medium-emphasis mb-6">요청하신 카드가 없거나 더 이상 공개되지 않아요.</p>
      <v-btn color="primary" variant="flat" to="/home">피드로 돌아가기</v-btn>
    </div>

    <v-alert v-else-if="isError" type="error" variant="tonal">카드를 불러오지 못했어요.</v-alert>

    <div v-else-if="card">
      <v-btn variant="text" prepend-icon="mdi-arrow-left" class="mb-4" @click="router.back()">뒤로</v-btn>

      <v-row>
        <v-col cols="12" md="8">
          <v-chip size="small" variant="tonal" color="primary" class="mb-3">{{ card.category }}</v-chip>
          <h1 class="display-xl text-balance mb-4">{{ card.title }}</h1>

          <v-alert v-if="!card.sourceAccessible" type="warning" variant="tonal" class="mb-4">
            원본 접근 불가 — 점수 갱신이 중단된 카드예요.
          </v-alert>

          <ul class="summary-list mb-6">
            <li v-for="(s, i) in summaries" :key="i" class="lead mb-1">{{ s }}</li>
          </ul>

          <v-btn :href="card.repoUrl" target="_blank" rel="noopener" variant="outlined" prepend-icon="mdi-github">
            원본 저장소 열기
          </v-btn>
        </v-col>

        <v-col cols="12" md="4">
          <v-card class="pa-6">
            <div class="d-flex flex-column align-center mb-4">
              <ScoreRing :score="card.scoreTotal" :size="120" />
              <span class="text-medium-emphasis mt-2">실전점수</span>
            </div>
            <div v-for="a in axes" :key="a.label" class="mb-3">
              <div class="d-flex justify-space-between text-body-2 mb-1">
                <span>{{ a.label }}</span><span class="text-medium-emphasis">{{ a.value }}</span>
              </div>
              <v-progress-linear :model-value="(a.value / 25) * 100" color="primary" height="6" rounded />
            </div>
          </v-card>
        </v-col>
      </v-row>

      <v-divider class="my-8" />

      <div class="d-flex align-center ga-6 mb-6 text-medium-emphasis">
        <span><v-icon icon="mdi-star-outline" /> {{ card.starCount }} 스타</span>
        <span><v-icon icon="mdi-heart-outline" /> {{ card.likeCount }} 좋아요</span>
        <span><v-icon icon="mdi-bookmark-outline" /> {{ card.bookmarkCount }} 북마크</span>
        <span><v-icon icon="mdi-comment-outline" /> {{ card.commentCount }} 댓글</span>
      </div>

      <v-card class="pa-6" color="surface-variant">
        <p class="text-medium-emphasis">
          좋아요·북마크·댓글 기능은 Phase 4에서 연결됩니다.
        </p>
      </v-card>
    </div>
  </v-container>
</template>

<style scoped>
.summary-list {
  list-style: none;
  padding: 0;
}
.summary-list li {
  padding-left: 1.25rem;
  position: relative;
}
.summary-list li::before {
  content: '—';
  position: absolute;
  left: 0;
  color: rgb(var(--v-theme-primary));
}
</style>
