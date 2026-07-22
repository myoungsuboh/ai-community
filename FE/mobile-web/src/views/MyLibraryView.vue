<script setup>
import { computed, ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { useInfiniteQuery } from '@tanstack/vue-query'
import { libraryApi } from '@/api/activity'
import { useAuthStore } from '@/stores/auth'
import CardTile from '@/components/CardTile.vue'

const auth = useAuthStore()
const userId = computed(() => auth.user?.id)

const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isLoading, isError, refetch } =
  useInfiniteQuery({
    queryKey: computed(() => ['library', userId.value]),
    queryFn: ({ pageParam = 0 }) => libraryApi.bookmarks(userId.value, { page: pageParam, size: 12 }),
    getNextPageParam: (lastPage) => (lastPage.hasMore ? lastPage.page + 1 : undefined),
    initialPageParam: 0,
    enabled: computed(() => !!userId.value),
  })

const cards = computed(() => data.value?.pages.flatMap((p) => p.content) ?? [])
const isEmpty = computed(() => !isLoading.value && cards.value.length === 0)

// 무한 스크롤: IntersectionObserver (virtual-scroll 스킬)
const sentinel = ref(null)
let observer = null
onMounted(() => {
  observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting && hasNextPage.value && !isFetchingNextPage.value) {
      fetchNextPage()
    }
  })
  watch(sentinel, (el) => {
    observer.disconnect()
    if (el) observer.observe(el)
  }, { immediate: true })
})
onBeforeUnmount(() => observer && observer.disconnect())
</script>

<template>
  <v-container class="container-max section">
    <p class="eyebrow text-primary mb-2">내 서재</p>
    <h1 class="display-xl mb-8">저장한 카드</h1>

    <div v-if="isLoading">
      <v-row>
        <v-col v-for="n in 3" :key="n" cols="12" sm="6" lg="4"><v-skeleton-loader type="card" /></v-col>
      </v-row>
    </div>

    <v-alert v-else-if="isError" type="error" variant="tonal">
      서재를 불러오지 못했어요.
      <template #append><v-btn size="small" variant="text" @click="refetch()">다시 시도</v-btn></template>
    </v-alert>

    <!-- POL-24: 빈 서재 -->
    <div v-else-if="isEmpty" class="text-center py-16">
      <v-icon icon="mdi-bookmark-outline" size="48" class="text-medium-emphasis mb-4" />
      <p class="display-md mb-2">아직 저장한 카드가 없어요.</p>
      <p class="text-medium-emphasis mb-6">피드에서 북마크를 눌러보세요.</p>
      <v-btn color="primary" variant="flat" to="/home">피드로 가기</v-btn>
    </div>

    <template v-else>
      <v-row aria-live="polite">
        <v-col v-for="card in cards" :key="card.cardId" cols="12" sm="6" lg="4">
          <!-- POL-22: 반려된 카드 -->
          <v-card v-if="card.status === 'REJECTED'" class="pa-5 h-100 d-flex flex-column justify-center align-center text-center" variant="tonal">
            <v-icon icon="mdi-eye-off-outline" class="mb-2 text-medium-emphasis" />
            <p class="text-medium-emphasis">더 이상 공개되지 않는 카드예요</p>
          </v-card>
          <CardTile v-else :card="card" />
        </v-col>
      </v-row>

      <div ref="sentinel" class="text-center py-6">
        <v-progress-circular v-if="isFetchingNextPage" indeterminate color="primary" />
      </div>
    </template>
  </v-container>
</template>
