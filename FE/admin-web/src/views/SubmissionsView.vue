<script setup>
import { ref, computed } from 'vue'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { submissionsApi } from '@/api/submissions'
import { apiMessage } from '@/api/http'

const qc = useQueryClient()

const { data, isLoading, isError, refetch } = useQuery({
  queryKey: ['submissions', 'PENDING'],
  queryFn: () => submissionsApi.list({ status: 'PENDING', page: 0, size: 50 }),
})
const submissions = computed(() => data.value?.content ?? [])

const feedback = ref({ show: false, color: 'success', text: '' })
function notify(text, color = 'success') {
  feedback.value = { show: true, color, text }
}

const { mutate: review, isPending } = useMutation({
  mutationFn: ({ id, payload }) => submissionsApi.review(id, payload),
  onSuccess: (res) => {
    qc.invalidateQueries({ queryKey: ['submissions'] })
    notify(res.status === 'PUBLISHED' ? '카드로 발행했어요.' : '제보를 반려했어요.')
    closeDialogs()
  },
  onError: (e) => notify(apiMessage(e, '처리에 실패했어요.'), 'error'),
})

// 발행 다이얼로그
const publishDlg = ref(false)
const current = ref(null)
const pub = ref({ title: '', category: 'Agent', s1: '', s2: '', s3: '', docs: 20, activity: 20, popularity: 20, maintenance: 20 })
const categories = ['Agent', 'LLM', 'RAG', 'Vision', 'Tooling', 'MLOps']
const total = computed(() => pub.value.docs + pub.value.activity + pub.value.popularity + pub.value.maintenance)

function openPublish(s) {
  current.value = s
  pub.value = { title: '', category: 'Agent', s1: '', s2: '', s3: '', docs: 20, activity: 20, popularity: 20, maintenance: 20 }
  publishDlg.value = true
}
function submitPublish() {
  review({
    id: current.value.submissionId,
    payload: {
      action: 'PUBLISH',
      title: pub.value.title,
      category: pub.value.category,
      summaryLine1: pub.value.s1,
      summaryLine2: pub.value.s2,
      summaryLine3: pub.value.s3,
      repoUrl: current.value.url,
      scoreAxisDocs: pub.value.docs,
      scoreAxisActivity: pub.value.activity,
      scoreAxisPopularity: pub.value.popularity,
      scoreAxisMaintenance: pub.value.maintenance,
    },
  })
}

// 반려 다이얼로그
const rejectDlg = ref(false)
const rejectReason = ref('')
function openReject(s) {
  current.value = s
  rejectReason.value = ''
  rejectDlg.value = true
}
function submitReject() {
  if (!rejectReason.value.trim()) {
    notify('반려 사유를 입력해 주세요.', 'error')
    return
  }
  review({ id: current.value.submissionId, payload: { action: 'REJECT', rejectionReason: rejectReason.value } })
}

function closeDialogs() {
  publishDlg.value = false
  rejectDlg.value = false
}
function fmt(d) {
  return d ? new Date(d).toLocaleString('ko-KR') : ''
}
</script>

<template>
  <v-container class="container-max section">
    <p class="eyebrow text-primary mb-2">검수함</p>
    <h1 class="display-xl mb-8">제보 검수</h1>

    <div v-if="isLoading"><v-skeleton-loader type="table" /></div>
    <v-alert v-else-if="isError" type="error" variant="tonal">
      목록을 불러오지 못했어요.
      <template #append><v-btn size="small" variant="text" @click="refetch()">다시 시도</v-btn></template>
    </v-alert>
    <div v-else-if="submissions.length === 0" class="text-center py-16">
      <v-icon icon="mdi-check-circle-outline" size="48" color="success" class="mb-4" />
      <p class="display-md">검수 대기 중인 제보가 없어요.</p>
    </div>

    <v-card v-else>
      <v-table>
        <thead>
          <tr>
            <th>URL</th><th>제보일</th><th class="text-right">처리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in submissions" :key="s.submissionId">
            <td>
              <a :href="s.url" target="_blank" rel="noopener" class="text-primary">{{ s.url }}</a>
            </td>
            <td class="text-medium-emphasis">{{ fmt(s.createdAt) }}</td>
            <td class="text-right">
              <v-btn size="small" color="primary" variant="flat" class="mr-2" @click="openPublish(s)">발행</v-btn>
              <v-btn size="small" color="error" variant="tonal" @click="openReject(s)">반려</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- 발행 다이얼로그 -->
    <v-dialog v-model="publishDlg" max-width="640">
      <v-card class="pa-6">
        <h2 class="display-md mb-1">카드 발행</h2>
        <p class="text-body-2 text-medium-emphasis mb-4">{{ current?.url }}</p>
        <v-text-field v-model="pub.title" label="카드 제목" class="mb-2" />
        <v-select v-model="pub.category" :items="categories" label="카테고리" class="mb-2" />
        <v-text-field v-model="pub.s1" label="요약 1줄 (최대 60자)" counter="60" class="mb-2" />
        <v-text-field v-model="pub.s2" label="요약 2줄" counter="60" class="mb-2" />
        <v-text-field v-model="pub.s3" label="요약 3줄" counter="60" class="mb-4" />
        <p class="text-body-2 mb-2">실전점수 4축 (총점 {{ total }})</p>
        <v-row dense>
          <v-col cols="6"><v-text-field v-model.number="pub.docs" type="number" label="문서화" /></v-col>
          <v-col cols="6"><v-text-field v-model.number="pub.activity" type="number" label="활성도" /></v-col>
          <v-col cols="6"><v-text-field v-model.number="pub.popularity" type="number" label="인기도" /></v-col>
          <v-col cols="6"><v-text-field v-model.number="pub.maintenance" type="number" label="유지보수" /></v-col>
        </v-row>
        <div class="d-flex justify-end ga-2 mt-4">
          <v-btn variant="text" @click="publishDlg = false">취소</v-btn>
          <v-btn color="primary" variant="flat" :loading="isPending" @click="submitPublish">발행</v-btn>
        </div>
      </v-card>
    </v-dialog>

    <!-- 반려 다이얼로그 -->
    <v-dialog v-model="rejectDlg" max-width="520">
      <v-card class="pa-6">
        <h2 class="display-md mb-4">제보 반려</h2>
        <v-textarea v-model="rejectReason" label="반려 사유 (필수)" rows="3" />
        <div class="d-flex justify-end ga-2 mt-2">
          <v-btn variant="text" @click="rejectDlg = false">취소</v-btn>
          <v-btn color="error" variant="flat" :loading="isPending" @click="submitReject">반려</v-btn>
        </div>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="feedback.show" :color="feedback.color" timeout="3000" location="top">
      {{ feedback.text }}
    </v-snackbar>
  </v-container>
</template>
