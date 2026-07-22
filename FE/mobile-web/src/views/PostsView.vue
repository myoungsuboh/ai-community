<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { postsApi } from '@/api/posts'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const keyword = ref('')
const search = ref('')

const { data, isLoading, isError, refetch } = useQuery({
  queryKey: computed(() => ['posts', search.value]),
  queryFn: () => postsApi.list({ keyword: search.value || undefined, page: 0, size: 20 }),
})
const posts = computed(() => data.value?.content ?? [])

function doSearch() {
  search.value = keyword.value.trim()
}
function goNew() {
  if (!auth.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: '/posts/new' } })
    return
  }
  router.push('/posts/new')
}
function fmt(d) {
  return d ? new Date(d).toLocaleDateString('ko-KR') : ''
}
</script>

<template>
  <v-container class="container-max section">
    <div class="d-flex flex-wrap justify-space-between align-center mb-6 ga-4">
      <div>
        <p class="eyebrow text-primary mb-2">커뮤니티</p>
        <h1 class="display-xl">게시글</h1>
      </div>
      <v-btn color="primary" variant="flat" size="large" prepend-icon="mdi-pencil" @click="goNew">
        새 게시글 작성
      </v-btn>
    </div>

    <v-text-field
      v-model="keyword"
      placeholder="키워드로 검색"
      prepend-inner-icon="mdi-magnify"
      clearable
      class="mb-6"
      @keyup.enter="doSearch"
      @click:clear="doSearch"
    />

    <div v-if="isLoading">
      <v-skeleton-loader v-for="n in 4" :key="n" type="list-item-two-line" />
    </div>
    <v-alert v-else-if="isError" type="error" variant="tonal">
      게시글을 불러오지 못했어요.
      <template #append><v-btn size="small" variant="text" @click="refetch()">다시 시도</v-btn></template>
    </v-alert>
    <div v-else-if="posts.length === 0" class="text-center py-16">
      <p class="display-md mb-2">검색 결과가 없습니다.</p>
      <p class="text-medium-emphasis">첫 게시글을 작성해 보세요.</p>
    </div>
    <v-row v-else>
      <v-col v-for="p in posts" :key="p.postId" cols="12">
        <v-card class="pa-5 card-hover" @click="router.push(`/posts/${p.postId}`)">
          <h3 class="display-md mb-2">{{ p.title }}</h3>
          <div class="d-flex flex-wrap align-center ga-2">
            <v-chip v-for="t in p.tags" :key="t" size="x-small" variant="tonal">#{{ t }}</v-chip>
            <v-spacer />
            <span class="text-body-2 text-medium-emphasis">{{ fmt(p.createdAt) }}</span>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
