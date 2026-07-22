<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { qnaApi } from '@/api/qna'
import { useAuthStore } from '@/stores/auth'
import { apiMessage } from '@/api/http'
import QnaQuestionPanel from '@/components/QnaQuestionPanel.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const qc = useQueryClient()

const keyword = ref('')
const search = ref('')
const { data, isLoading, isError, refetch } = useQuery({
  queryKey: computed(() => ['questions', search.value]),
  queryFn: () => qnaApi.list({ keyword: search.value || undefined, page: 0, size: 20 }),
})
const questions = computed(() => data.value?.content ?? [])

// 질문 등록 다이얼로그
const dialog = ref(false)
const form = ref({ title: '', content: '' })
const errorMsg = ref('')
const { mutate: createQuestion, isPending } = useMutation({
  mutationFn: (payload) => qnaApi.createQuestion(payload),
  onSuccess: () => {
    dialog.value = false
    form.value = { title: '', content: '' }
    qc.invalidateQueries({ queryKey: ['questions'] })
  },
  onError: (e) => (errorMsg.value = apiMessage(e, '질문 등록에 실패했어요.')),
})

function openAsk() {
  if (!auth.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  errorMsg.value = ''
  dialog.value = true
}
function submitQuestion() {
  errorMsg.value = ''
  if (!form.value.title.trim() || !form.value.content.trim()) {
    errorMsg.value = '제목과 내용을 입력해 주세요.'
    return
  }
  createQuestion({ title: form.value.title, content: form.value.content })
}
function doSearch() {
  search.value = keyword.value.trim()
}
function fmt(d) {
  return d ? new Date(d).toLocaleDateString('ko-KR') : ''
}
</script>

<template>
  <v-container class="container-max section">
    <div class="d-flex flex-wrap justify-space-between align-center mb-6 ga-4">
      <div>
        <p class="eyebrow text-primary mb-2">함께 배우기</p>
        <h1 class="display-xl">Q&amp;A</h1>
      </div>
      <v-btn color="primary" variant="flat" size="large" prepend-icon="mdi-help-circle-outline" @click="openAsk">
        질문하기
      </v-btn>
    </div>

    <v-text-field
      v-model="keyword" placeholder="질문 검색" prepend-inner-icon="mdi-magnify" clearable class="mb-6"
      @keyup.enter="doSearch" @click:clear="doSearch"
    />

    <div v-if="isLoading"><v-skeleton-loader v-for="n in 4" :key="n" type="list-item-two-line" /></div>
    <v-alert v-else-if="isError" type="error" variant="tonal">
      질문을 불러오지 못했어요.
      <template #append><v-btn size="small" variant="text" @click="refetch()">다시 시도</v-btn></template>
    </v-alert>
    <div v-else-if="questions.length === 0" class="text-center py-16">
      <p class="display-md mb-2">아직 질문이 없어요.</p>
      <p class="text-medium-emphasis">첫 질문을 남겨보세요.</p>
    </div>

    <v-expansion-panels v-else variant="accordion">
      <v-expansion-panel v-for="q in questions" :key="q.questionId">
        <v-expansion-panel-title>
          <div class="d-flex flex-column">
            <span class="font-weight-bold">{{ q.title }}</span>
            <span class="text-caption text-medium-emphasis">답변 {{ q.answerCount }} · {{ fmt(q.createdAt) }}</span>
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <QnaQuestionPanel :question-id="q.questionId" />
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>

    <!-- 질문 등록 다이얼로그 -->
    <v-dialog v-model="dialog" max-width="600">
      <v-card class="pa-6">
        <h2 class="display-md mb-4">질문하기</h2>
        <v-alert v-if="errorMsg" type="error" variant="tonal" density="compact" class="mb-3">{{ errorMsg }}</v-alert>
        <v-text-field v-model="form.title" label="제목" class="mb-2" />
        <v-textarea v-model="form.content" label="질문 내용" rows="5" class="mb-2" />
        <div class="d-flex justify-end ga-2">
          <v-btn variant="text" @click="dialog = false">취소</v-btn>
          <v-btn color="primary" variant="flat" :loading="isPending" @click="submitQuestion">등록</v-btn>
        </div>
      </v-card>
    </v-dialog>
  </v-container>
</template>
