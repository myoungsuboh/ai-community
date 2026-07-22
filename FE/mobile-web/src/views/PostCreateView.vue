<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { postsApi } from '@/api/posts'
import { apiMessage } from '@/api/http'

const router = useRouter()
const qc = useQueryClient()

const title = ref('')
const content = ref('')
const tagsInput = ref('')
const errorMsg = ref('')

const titleRules = [(v) => (v || '').length >= 5 || '제목은 5자 이상이어야 합니다.']
const contentRules = [(v) => (v || '').length >= 10 || '내용은 10자 이상이어야 합니다.']

const { mutate, isPending } = useMutation({
  mutationFn: (payload) => postsApi.create(payload),
  onSuccess: (data) => {
    qc.invalidateQueries({ queryKey: ['posts'] })
    router.push(`/posts/${data.postId}`)
  },
  onError: (e) => {
    errorMsg.value = apiMessage(e, '게시글 작성에 실패했어요.')
  },
})

function submit() {
  errorMsg.value = ''
  if ((title.value || '').length < 5 || (content.value || '').length < 10) {
    errorMsg.value = '제목 5자 이상, 내용 10자 이상 입력해 주세요.'
    return
  }
  const tags = tagsInput.value
    .split(',')
    .map((t) => t.trim())
    .filter(Boolean)
  mutate({ title: title.value, content: content.value, tags })
}
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" md="8">
        <p class="eyebrow text-primary mb-2">새 게시글</p>
        <h1 class="display-xl mb-8">지식을 나눠요.</h1>

        <v-alert v-if="errorMsg" type="error" variant="tonal" class="mb-4">{{ errorMsg }}</v-alert>

        <v-form @submit.prevent="submit">
          <v-text-field v-model="title" label="제목 (5자 이상)" :rules="titleRules" class="mb-3" />
          <v-textarea v-model="content" label="내용 (10자 이상)" :rules="contentRules" rows="10" class="mb-3" />
          <v-text-field
            v-model="tagsInput"
            label="태그 (쉼표로 구분)"
            placeholder="예: RAG, 임베딩, 검색"
            class="mb-4"
          />
          <div class="d-flex ga-3">
            <v-btn type="submit" color="primary" variant="flat" size="large" :loading="isPending">발행</v-btn>
            <v-btn variant="text" size="large" to="/posts">취소</v-btn>
          </div>
        </v-form>
      </v-col>
    </v-row>
  </v-container>
</template>
