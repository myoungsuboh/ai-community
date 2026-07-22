<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { apiMessage } from '@/api/http'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const form = ref({ email: '', password: '' })
const loading = ref(false)
const errorMsg = ref(route.query.denied ? '큐레이터 권한이 필요한 페이지예요.' : '')

const emailRules = [(v) => !!v || '이메일을 입력해 주세요.', (v) => /.+@.+\..+/.test(v) || '올바른 이메일 형식이 아니에요.']
const pwRules = [(v) => !!v || '비밀번호를 입력해 주세요.']

async function submit() {
  errorMsg.value = ''
  loading.value = true
  try {
    const user = await auth.login(form.value.email, form.value.password)
    if (!(user.role === 'CURATOR' || user.role === 'ADMIN')) {
      errorMsg.value = '큐레이터/관리자 계정만 접속할 수 있어요.'
      await auth.logout()
      return
    }
    const target = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
    router.push(target)
  } catch (e) {
    errorMsg.value = apiMessage(e, '로그인에 실패했어요.')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" sm="8" md="5">
        <p class="eyebrow text-primary mb-3">큐레이터 콘솔</p>
        <h1 class="display-xl mb-8">로그인</h1>

        <v-alert v-if="errorMsg" type="error" variant="tonal" class="mb-4">{{ errorMsg }}</v-alert>

        <v-card class="pa-6 pa-sm-8">
          <v-form @submit.prevent="submit">
            <v-text-field v-model="form.email" label="이메일" :rules="emailRules" class="mb-2" />
            <v-text-field v-model="form.password" label="비밀번호" type="password" :rules="pwRules" class="mb-4" />
            <v-btn type="submit" block size="large" color="primary" variant="flat" :loading="loading">
              로그인
            </v-btn>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
