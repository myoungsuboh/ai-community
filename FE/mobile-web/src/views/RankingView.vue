<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { rankingApi } from '@/api/ranking'

const router = useRouter()

const { data, isLoading, isError, refetch } = useQuery({
  queryKey: ['ranking-weekly'],
  queryFn: () => rankingApi.weekly(),
})
const entries = computed(() => data.value?.entries ?? [])
const period = computed(() => (data.value?.year ? `${data.value.year}년 ${data.value.week}주차` : ''))

function medal(rank) {
  return { 1: '🥇', 2: '🥈', 3: '🥉' }[rank] || null
}
</script>

<template>
  <v-container class="container-max section">
    <p class="eyebrow text-primary mb-2">이번 주 트렌드</p>
    <h1 class="display-xl mb-2">주간 랭킹</h1>
    <p v-if="period" class="text-medium-emphasis mb-8">{{ period }} 기준</p>

    <div v-if="isLoading"><v-skeleton-loader v-for="n in 5" :key="n" type="list-item-two-line" /></div>
    <v-alert v-else-if="isError" type="error" variant="tonal">
      랭킹을 불러오지 못했어요.
      <template #append><v-btn size="small" variant="text" @click="refetch()">다시 시도</v-btn></template>
    </v-alert>

    <div v-else-if="entries.length === 0" class="text-center py-16">
      <v-icon icon="mdi-trophy-outline" size="48" class="text-medium-emphasis mb-4" />
      <p class="display-md mb-2">아직 이번 주 랭킹이 없어요.</p>
      <p class="text-medium-emphasis">매주 월요일 0시에 새 랭킹이 만들어져요.</p>
    </div>

    <v-card v-else>
      <v-list lines="two">
        <template v-for="(e, i) in entries" :key="e.cardId">
          <v-divider v-if="i > 0" />
          <v-list-item class="py-3" @click="router.push(`/cards/${e.slug}`)">
            <template #prepend>
              <div class="rank-badge font-display">
                <span v-if="medal(e.rank)" class="rank-medal">{{ medal(e.rank) }}</span>
                <span v-else>{{ e.rank }}</span>
              </div>
            </template>
            <v-list-item-title class="font-weight-bold">{{ e.title }}</v-list-item-title>
            <v-list-item-subtitle>
              <v-chip size="x-small" variant="tonal" color="primary" class="mr-2">{{ e.category }}</v-chip>
              <span class="text-medium-emphasis">
                ❤️ {{ e.likeCount }} · 🔖 {{ e.bookmarkCount }} · 💬 {{ e.commentCount }}
              </span>
            </v-list-item-subtitle>
            <template #append>
              <div class="text-right">
                <div class="display-md text-primary">{{ e.score }}</div>
                <div class="text-caption text-medium-emphasis">점수</div>
              </div>
            </template>
          </v-list-item>
        </template>
      </v-list>
    </v-card>
  </v-container>
</template>

<style scoped>
.rank-badge {
  width: 40px;
  text-align: center;
  font-weight: 800;
  font-size: 1.25rem;
  margin-right: 8px;
}
.rank-medal {
  font-size: 1.5rem;
}
</style>
