<script setup>
import { ref } from 'vue'
import { useMutation } from '@tanstack/vue-query'
import { submissionsApi } from '@/api/submissions'
import { apiMessage } from '@/api/http'

const url = ref('')
const errorMsg = ref('')
const done = ref(false)

const urlRules = [
  (v) => !!v || 'URL을 입력해 주세요.',
  (v) => /^https?:\/\/.+/.test(v || '') || 'http(s):// 로 시작하는 URL을 입력해 주세요.',
]

const { mutate, isPending } = useMutation({
  mutationFn: (u) => submissionsApi.create(u),
  onSuccess: () => {
    done.value = true
    url.value = ''
  },
  onError: (e) => {
    // 422 잘못된 URL / 409 중복 / 429 하루 5건 초과("내일 다시 제보할 수 있어요")
    errorMsg.value = apiMessage(e, '제보에 실패했어요.')
  },
})

function submit() {
  errorMsg.value = ''
  done.value = false
  if (!/^https?:\/\/.+/.test(url.value || '')) {
    errorMsg.value = 'http(s):// 로 시작하는 URL을 입력해 주세요.'
    return
  }
  mutate(url.value)
}
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" md="7">
        <p class="eyebrow text-primary mb-2">기여하기</p>
        <h1 class="display-xl mb-4">좋은 AI를 제보해요.</h1>
        <p class="lead text-medium-emphasis mb-8">
          알고 있는 AI 레포·스킬의 URL을 알려주세요. 큐레이터 검수 후 카드로 발행됩니다.
        </p>

        <v-alert v-if="done" type="success" variant="tonal" class="mb-4">
          제보가 접수되었어요! 큐레이터 검수 후 발행됩니다.
        </v-alert>
        <v-alert v-if="errorMsg" type="error" variant="tonal" class="mb-4">{{ errorMsg }}</v-alert>

        <v-card class="pa-6 pa-sm-8">
          <v-form @submit.prevent="submit">
            <v-text-field
              v-model="url"
              label="레포/스킬 URL"
              placeholder="https://github.com/..."
              :rules="urlRules"
              class="mb-4"
            />
            <v-btn type="submit" color="primary" variant="flat" size="large" block :loading="isPending">
              제보하기
            </v-btn>
          </v-form>
        </v-card>
        <p class="text-caption text-medium-emphasis mt-3">하루 최대 5건까지 제보할 수 있어요.</p>
      </v-col>
    </v-row>
  </v-container>
</template>
