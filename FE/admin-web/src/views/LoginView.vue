<script setup>
import { ref } from 'vue'

// Phase 1: 큐레이터 로그인 폼 골격. 실제 인증 연동은 Phase 2 (auth-service).
const form = ref({ email: '', password: '' })
const info = ref(false)

const emailRules = [(v) => !!v || '이메일을 입력해 주세요.', (v) => /.+@.+\..+/.test(v) || '올바른 이메일 형식이 아니에요.']
const pwRules = [(v) => !!v || '비밀번호를 입력해 주세요.']

function submit() {
  info.value = true
}
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" sm="8" md="5">
        <p class="eyebrow text-primary mb-3">큐레이터 콘솔</p>
        <h1 class="display-xl mb-8">로그인</h1>

        <v-card class="pa-6 pa-sm-8">
          <v-form @submit.prevent="submit">
            <v-text-field v-model="form.email" label="이메일" :rules="emailRules" class="mb-2" />
            <v-text-field v-model="form.password" label="비밀번호" type="password" :rules="pwRules" class="mb-4" />
            <v-btn type="submit" block size="large" color="primary" variant="flat">로그인</v-btn>
          </v-form>
        </v-card>

        <v-alert v-if="info" type="info" variant="tonal" class="mt-4" density="comfortable">
          로그인 기능은 Phase 2에서 백엔드와 연결됩니다.
        </v-alert>
      </v-col>
    </v-row>
  </v-container>
</template>
