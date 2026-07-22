<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { postsApi } from '@/api/posts'

const route = useRoute()
const router = useRouter()
const id = computed(() => route.params.postId)

const { data: post, isLoading, isError, error } = useQuery({
  queryKey: computed(() => ['post', id.value]),
  queryFn: () => postsApi.get(id.value),
  retry: false,
})
const notFound = computed(() => error.value?.response?.status === 404)
function fmt(d) {
  return d ? new Date(d).toLocaleString('ko-KR') : ''
}
</script>

<template>
  <v-container class="container-max section">
    <v-skeleton-loader v-if="isLoading" type="article" />
    <div v-else-if="notFound" class="text-center py-16">
      <h1 class="display-lg mb-3">게시글을 찾을 수 없어요</h1>
      <v-btn color="primary" variant="flat" to="/posts">목록으로</v-btn>
    </div>
    <v-alert v-else-if="isError" type="error" variant="tonal">게시글을 불러오지 못했어요.</v-alert>
    <article v-else-if="post">
      <v-btn variant="text" prepend-icon="mdi-arrow-left" class="mb-4" @click="router.push('/posts')">
        목록
      </v-btn>
      <h1 class="display-xl text-balance mb-3">{{ post.title }}</h1>
      <div class="d-flex flex-wrap align-center ga-2 mb-6">
        <v-chip v-for="t in post.tags" :key="t" size="small" variant="tonal">#{{ t }}</v-chip>
        <v-spacer />
        <span class="text-body-2 text-medium-emphasis">{{ fmt(post.createdAt) }}</span>
      </div>
      <p class="lead" style="white-space: pre-wrap">{{ post.content }}</p>
    </article>
  </v-container>
</template>
