<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { cardsApi } from '@/api/cards'
import { reactionsApi, commentsApi } from '@/api/activity'
import { useAuthStore } from '@/stores/auth'
import { apiMessage } from '@/api/http'
import ScoreRing from '@/components/ScoreRing.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const qc = useQueryClient()
const slug = computed(() => route.params.cardSlug)

const { data: card, isLoading, isError, error } = useQuery({
  queryKey: computed(() => ['card', slug.value]),
  queryFn: () => cardsApi.detail(slug.value),
  retry: false,
})
const notFound = computed(() => error.value?.response?.status === 404)
const cardId = computed(() => card.value?.cardId)

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

const snack = ref({ show: false, text: '', color: 'success' })
function notify(text, color = 'success') {
  snack.value = { show: true, text, color }
}
function requireLogin(redirect) {
  router.push({ name: 'login', query: { redirect } })
}

// 좋아요/북마크 — 응답의 isAdded 로 로컬 활성상태 반영. 카운트는 카드 재조회로 갱신.
const liked = ref(false)
const bookmarked = ref(false)
const { mutate: toggle, isPending: toggling } = useMutation({
  mutationFn: ({ type }) => reactionsApi.toggle(cardId.value, type),
  onSuccess: (res) => {
    if (res.type === 'LIKE') liked.value = res.isAdded
    else bookmarked.value = res.isAdded
    qc.invalidateQueries({ queryKey: ['card', slug.value] })
  },
  onError: (e) => notify(apiMessage(e, '처리에 실패했어요.'), 'error'),
})
function onReact(type) {
  if (!auth.isAuthenticated) return requireLogin(route.fullPath) // POL-20
  toggle({ type })
}

// 댓글
const commentsQuery = useQuery({
  queryKey: computed(() => ['comments', cardId.value]),
  queryFn: () => commentsApi.list(cardId.value, { page: 0, size: 50 }),
  enabled: computed(() => !!cardId.value),
})
const comments = computed(() => commentsQuery.data.value?.content ?? [])

const newComment = ref('')
const { mutate: postComment, isPending: posting } = useMutation({
  mutationFn: (content) => commentsApi.create(cardId.value, content),
  onSuccess: () => {
    newComment.value = ''
    qc.invalidateQueries({ queryKey: ['comments', cardId.value] })
    qc.invalidateQueries({ queryKey: ['card', slug.value] })
    notify('댓글을 남겼어요.')
  },
  onError: (e) => notify(apiMessage(e, '댓글 작성에 실패했어요.'), 'error'),
})
function submitComment() {
  if (!auth.isAuthenticated) return requireLogin(route.fullPath) // POL-25
  if (!newComment.value.trim()) return
  postComment(newComment.value.trim())
}

const { mutate: report } = useMutation({
  mutationFn: (commentId) => commentsApi.report(commentId),
  onSuccess: (res) => {
    qc.invalidateQueries({ queryKey: ['comments', cardId.value] })
    notify(res.hidden ? '신고가 누적되어 댓글이 가려졌어요.' : '신고했어요.')
  },
  onError: (e) => notify(apiMessage(e, '신고에 실패했어요.'), 'error'),
})
function onReport(commentId) {
  if (!auth.isAuthenticated) return requireLogin(route.fullPath)
  report(commentId)
}
function fmt(d) {
  return d ? new Date(d).toLocaleString('ko-KR') : ''
}
</script>

<template>
  <v-container class="container-max section">
    <div v-if="isLoading"><v-skeleton-loader type="article, list-item-three-line" /></div>

    <div v-else-if="notFound" class="text-center py-16">
      <h1 class="display-lg mb-3">카드를 찾을 수 없어요</h1>
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

          <div class="d-flex flex-wrap ga-3 mb-2">
            <v-btn :href="card.repoUrl" target="_blank" rel="noopener" variant="outlined" prepend-icon="mdi-github">
              원본 저장소
            </v-btn>
            <v-btn
              :color="liked ? 'secondary' : undefined" :variant="liked ? 'flat' : 'tonal'"
              :loading="toggling" prepend-icon="mdi-heart" @click="onReact('LIKE')"
            >
              좋아요 {{ card.likeCount }}
            </v-btn>
            <v-btn
              :color="bookmarked ? 'primary' : undefined" :variant="bookmarked ? 'flat' : 'tonal'"
              :loading="toggling" prepend-icon="mdi-bookmark" @click="onReact('BOOKMARK')"
            >
              북마크 {{ card.bookmarkCount }}
            </v-btn>
          </div>
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

      <!-- 댓글 -->
      <h2 class="display-lg mb-4">댓글 {{ card.commentCount }}</h2>

      <div class="mb-6">
        <template v-if="auth.isAuthenticated">
          <v-textarea v-model="newComment" placeholder="댓글을 남겨보세요 (최대 500자)" counter="500" rows="3" auto-grow />
          <div class="d-flex justify-end">
            <v-btn color="primary" variant="flat" :loading="posting" @click="submitComment">등록</v-btn>
          </div>
        </template>
        <v-btn v-else variant="tonal" @click="requireLogin(route.fullPath)">로그인하고 댓글 달기</v-btn>
      </div>

      <div v-if="commentsQuery.isLoading.value"><v-skeleton-loader type="list-item-two-line" /></div>
      <p v-else-if="comments.length === 0" class="text-medium-emphasis py-4">아직 댓글이 없어요. 첫 댓글을 남겨보세요.</p>
      <div v-else>
        <v-card v-for="c in comments" :key="c.commentId" class="pa-4 mb-3" variant="tonal">
          <div class="d-flex justify-space-between align-start">
            <p class="mb-1" style="white-space: pre-wrap">{{ c.content }}</p>
            <v-btn icon="mdi-flag-outline" size="x-small" variant="text" title="신고" @click="onReport(c.commentId)" />
          </div>
          <span class="text-caption text-medium-emphasis">{{ fmt(c.createdAt) }}</span>
        </v-card>
      </div>
    </div>

    <v-snackbar v-model="snack.show" :color="snack.color" timeout="3000" location="top">{{ snack.text }}</v-snackbar>
  </v-container>
</template>

<style scoped>
.summary-list { list-style: none; padding: 0; }
.summary-list li { padding-left: 1.25rem; position: relative; }
.summary-list li::before { content: '—'; position: absolute; left: 0; color: rgb(var(--v-theme-primary)); }
</style>
