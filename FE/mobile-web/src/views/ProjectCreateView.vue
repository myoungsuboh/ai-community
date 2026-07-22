<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { projectsApi } from '@/api/projects'
import { apiMessage } from '@/api/http'

const router = useRouter()
const qc = useQueryClient()

const form = ref({ name: '', description: '', tags: '' })
const errorMsg = ref('')

const { mutate, isPending } = useMutation({
  mutationFn: (payload) => projectsApi.create(payload),
  onSuccess: (data) => {
    qc.invalidateQueries({ queryKey: ['projects'] })
    router.push(`/projects/${data.projectId}`)
  },
  onError: (e) => (errorMsg.value = apiMessage(e, '프로젝트 생성에 실패했어요.')),
})

function submit() {
  errorMsg.value = ''
  if ((form.value.name || '').trim().length < 2) {
    errorMsg.value = '프로젝트명은 2자 이상이어야 합니다.'
    return
  }
  const tags = form.value.tags.split(',').map((t) => t.trim()).filter(Boolean)
  mutate({ name: form.value.name, description: form.value.description, tags })
}
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" md="8">
        <p class="eyebrow text-primary mb-2">새 프로젝트</p>
        <h1 class="display-xl mb-8">함께 만들 팀을 모아요.</h1>

        <v-alert v-if="errorMsg" type="error" variant="tonal" class="mb-4">{{ errorMsg }}</v-alert>

        <v-form @submit.prevent="submit">
          <v-text-field v-model="form.name" label="프로젝트명 (2자 이상)" class="mb-3" />
          <v-textarea v-model="form.description" label="소개" rows="6" class="mb-3" />
          <v-text-field v-model="form.tags" label="태그 (쉼표로 구분)" placeholder="예: LLM, 에이전트" class="mb-4" />
          <div class="d-flex ga-3">
            <v-btn type="submit" color="primary" variant="flat" size="large" :loading="isPending">만들기</v-btn>
            <v-btn variant="text" size="large" to="/projects">취소</v-btn>
          </div>
        </v-form>
      </v-col>
    </v-row>
  </v-container>
</template>
