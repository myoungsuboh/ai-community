<script setup>
import { reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { cardsApi } from '@/api/cards'
import CardTile from '@/components/CardTile.vue'

const router = useRouter()

const categories = ['전체', 'Agent', 'LLM', 'RAG', 'Vision', 'Tooling', 'MLOps']
const sorts = [
  { label: '점수순', value: 'score' },
  { label: '최신순', value: 'recent' },
  { label: '스타순', value: 'stars' },
]

const filters = reactive({ category: '전체', minScore: 0, sort: 'score' })

const queryKey = computed(() => ['cards', filters.category, filters.minScore, filters.sort])

const { data, isLoading, isError, refetch } = useQuery({
  queryKey,
  queryFn: () =>
    cardsApi.list({
      category: filters.category === '전체' ? undefined : filters.category,
      minScore: filters.minScore || undefined,
      sort: filters.sort,
      page: 0,
      size: 12,
    }),
})

const cards = computed(() => data.value?.content ?? [])
</script>

<template>
  <div>
    <!-- 햇살 히어로 -->
    <section class="sunny-hero">
      <v-container class="container-max pt-12 pb-10">
        <p class="eyebrow mb-3">AI 지식 공유 커뮤니티</p>
        <h1 class="display-hero text-balance mb-4">지금의 AI를<br />한눈에, 함께.</h1>
        <p class="lead mb-6" style="max-width: 40rem">
          커뮤니티가 큐레이션한 AI 레포·스킬을 실전점수로 살펴보세요.
        </p>
        <div class="d-flex flex-wrap ga-3">
          <v-btn size="large" color="primary" variant="flat" @click="router.push('/submit')">
            좋은 AI 제보하기
          </v-btn>
          <v-btn size="large" variant="outlined" color="#2b2b2b" to="/qna">Q&amp;A 둘러보기</v-btn>
        </div>
      </v-container>
    </section>

    <!-- 필터 바 -->
    <v-container class="container-max py-4">
      <div class="d-flex flex-wrap align-center ga-2 mb-2">
        <v-chip
          v-for="c in categories" :key="c"
          :color="filters.category === c ? 'primary' : undefined"
          :variant="filters.category === c ? 'flat' : 'tonal'"
          @click="filters.category = c"
        >
          {{ c }}
        </v-chip>
      </div>
      <div class="d-flex flex-wrap align-center ga-4">
        <v-btn-toggle v-model="filters.sort" density="comfortable" variant="outlined" divided mandatory>
          <v-btn v-for="s in sorts" :key="s.value" :value="s.value" size="small">{{ s.label }}</v-btn>
        </v-btn-toggle>
        <div class="d-flex align-center ga-2" style="min-width: 220px">
          <span class="text-body-2 text-medium-emphasis">최소점수 {{ filters.minScore }}</span>
          <v-slider v-model="filters.minScore" :min="0" :max="100" :step="10" hide-details density="compact" />
        </div>
      </div>
    </v-container>

    <!-- 카드 그리드 -->
    <v-container class="container-max pb-16">
      <!-- 로딩 -->
      <v-row v-if="isLoading">
        <v-col v-for="n in 6" :key="n" cols="12" sm="6" lg="4">
          <v-skeleton-loader type="card" />
        </v-col>
      </v-row>

      <!-- 에러 -->
      <v-alert v-else-if="isError" type="error" variant="tonal" class="my-8">
        카드를 불러오지 못했어요.
        <template #append><v-btn size="small" variant="text" @click="refetch()">다시 시도</v-btn></template>
      </v-alert>

      <!-- 빈 상태 (POL-16) -->
      <div v-else-if="cards.length === 0" class="text-center py-16">
        <v-icon icon="mdi-card-search-outline" size="48" class="text-medium-emphasis mb-4" />
        <p class="display-md mb-2">검색 결과가 없습니다.</p>
        <p class="text-medium-emphasis">필터를 바꾸거나 새로운 AI를 제보해 보세요.</p>
        <v-btn class="mt-4" color="primary" variant="flat" @click="router.push('/submit')">제보하기</v-btn>
      </div>

      <!-- 결과 -->
      <v-row v-else>
        <v-col v-for="card in cards" :key="card.cardId" cols="12" sm="6" lg="4">
          <CardTile :card="card" />
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>
