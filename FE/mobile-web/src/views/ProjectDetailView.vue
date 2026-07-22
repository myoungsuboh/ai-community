<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { projectsApi, PROJECT_STATUS } from '@/api/projects'
import { useAuthStore } from '@/stores/auth'
import { apiMessage } from '@/api/http'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const qc = useQueryClient()
const id = computed(() => route.params.projectId)

const { data: project, isLoading, isError, error } = useQuery({
  queryKey: computed(() => ['project', id.value]),
  queryFn: () => projectsApi.get(id.value),
  retry: false,
})
const notFound = computed(() => error.value?.response?.status === 404)
const isOwner = computed(() => auth.user?.id && project.value?.ownerId === auth.user.id)

const statusOptions = [
  { value: 'RECRUITING', title: '팀원 모집 중' },
  { value: 'IN_PROGRESS', title: '진행 중' },
  { value: 'DONE', title: '완료' },
]
const editStatus = ref('RECRUITING')
const editProgress = ref('')
const snack = ref({ show: false, text: '', color: 'success' })
watch(project, (p) => {
  if (p) {
    editStatus.value = p.status
    editProgress.value = p.progress || ''
  }
})

const { mutate: saveStatus, isPending } = useMutation({
  mutationFn: () => projectsApi.updateStatus(id.value, { status: editStatus.value, progress: editProgress.value }),
  onSuccess: () => {
    qc.invalidateQueries({ queryKey: ['project', id.value] })
    qc.invalidateQueries({ queryKey: ['projects'] })
    snack.value = { show: true, text: '진행 상황을 업데이트했어요.', color: 'success' }
  },
  onError: (e) => (snack.value = { show: true, text: apiMessage(e, '업데이트 실패'), color: 'error' }),
})
</script>

<template>
  <v-container class="container-max section">
    <v-skeleton-loader v-if="isLoading" type="article" />
    <div v-else-if="notFound" class="text-center py-16">
      <h1 class="display-lg mb-3">프로젝트를 찾을 수 없어요</h1>
      <v-btn color="primary" variant="flat" to="/projects">목록으로</v-btn>
    </div>
    <v-alert v-else-if="isError" type="error" variant="tonal">불러오지 못했어요.</v-alert>

    <div v-else-if="project">
      <v-btn variant="text" prepend-icon="mdi-arrow-left" class="mb-4" @click="router.push('/projects')">목록</v-btn>
      <div class="d-flex flex-wrap justify-space-between align-center mb-4 ga-2">
        <h1 class="display-xl">{{ project.name }}</h1>
        <v-chip size="large" variant="tonal" color="primary">{{ PROJECT_STATUS[project.status] }}</v-chip>
      </div>
      <div class="d-flex flex-wrap ga-1 mb-6">
        <v-chip v-for="t in project.tags" :key="t" size="small" variant="tonal">#{{ t }}</v-chip>
      </div>
      <p class="lead mb-8" style="white-space: pre-wrap">{{ project.description }}</p>

      <v-card v-if="project.progress" class="pa-6 mb-6" color="surface-variant">
        <p class="eyebrow mb-2">진행 상황</p>
        <p style="white-space: pre-wrap">{{ project.progress }}</p>
      </v-card>

      <!-- 관리자만 진행상황 업데이트 -->
      <v-card v-if="isOwner" class="pa-6">
        <h2 class="display-md mb-4">진행 상황 업데이트</h2>
        <v-select v-model="editStatus" :items="statusOptions" label="상태" class="mb-3" />
        <v-textarea v-model="editProgress" label="진행 메모" rows="3" class="mb-3" />
        <v-btn color="primary" variant="flat" :loading="isPending" @click="saveStatus">저장</v-btn>
      </v-card>
    </div>

    <v-snackbar v-model="snack.show" :color="snack.color" timeout="3000" location="top">{{ snack.text }}</v-snackbar>
  </v-container>
</template>
