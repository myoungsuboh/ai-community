<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { qnaApi } from '@/api/qna'
import { useAuthStore } from '@/stores/auth'
import { apiMessage } from '@/api/http'

// 질문 상세 + 답변 (패널이 열릴 때 마운트되어 조회). 답변 작성 포함.
const props = defineProps({ questionId: { type: String, required: true } })

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const qc = useQueryClient()

const { data, isLoading, isError } = useQuery({
  queryKey: computed(() => ['question', props.questionId]),
  queryFn: () => qnaApi.detail(props.questionId),
})

const answer = ref('')
const errorMsg = ref('')
const { mutate, isPending } = useMutation({
  mutationFn: (content) => qnaApi.createAnswer(props.questionId, content),
  onSuccess: () => {
    answer.value = ''
    qc.invalidateQueries({ queryKey: ['question', props.questionId] })
    qc.invalidateQueries({ queryKey: ['questions'] })
  },
  onError: (e) => (errorMsg.value = apiMessage(e, '답변 등록에 실패했어요.')),
})

function submit() {
  errorMsg.value = ''
  if (!auth.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  if (!answer.value.trim()) return
  mutate(answer.value.trim())
}
function fmt(d) {
  return d ? new Date(d).toLocaleString('ko-KR') : ''
}
</script>

<template>
  <div>
    <div v-if="isLoading"><v-skeleton-loader type="paragraph" /></div>
    <v-alert v-else-if="isError" type="error" variant="tonal" density="compact">불러오지 못했어요.</v-alert>
    <template v-else-if="data">
      <p class="mb-4" style="white-space: pre-wrap">{{ data.content }}</p>

      <p class="text-subtitle-2 font-weight-bold mb-2">답변 {{ data.answerCount }}</p>
      <p v-if="data.answers.length === 0" class="text-medium-emphasis text-body-2 mb-3">
        아직 답변이 없어요. 첫 답변을 남겨보세요.
      </p>
      <v-card v-for="a in data.answers" :key="a.answerId" variant="tonal" class="pa-3 mb-2">
        <p class="mb-1" style="white-space: pre-wrap">{{ a.content }}</p>
        <span class="text-caption text-medium-emphasis">{{ fmt(a.createdAt) }}</span>
      </v-card>

      <v-alert v-if="errorMsg" type="error" variant="tonal" density="compact" class="mt-2">{{ errorMsg }}</v-alert>
      <div class="d-flex ga-2 mt-3 align-start">
        <v-textarea v-model="answer" placeholder="답변을 입력하세요" rows="2" auto-grow hide-details density="comfortable" />
        <v-btn color="primary" variant="flat" :loading="isPending" @click="submit">답변</v-btn>
      </div>
    </template>
  </div>
</template>
