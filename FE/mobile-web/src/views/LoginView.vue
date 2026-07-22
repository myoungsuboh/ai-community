<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { apiMessage } from '@/api/http'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const tab = ref('login')
const loading = ref(false)
const errorMsg = ref('')

const login = ref({ email: '', password: '' })
const register = ref({ email: '', password: '', nickname: '' })

const emailRules = [(v) => !!v || '이메일을 입력해 주세요.', (v) => /.+@.+\..+/.test(v) || '올바른 이메일 형식이 아니에요.']
const pwRules = [(v) => !!v || '비밀번호를 입력해 주세요.', (v) => (v || '').length >= 8 || '비밀번호는 8자 이상이에요.']
const nickRules = [(v) => !!v || '닉네임을 입력해 주세요.']

function redirectAfter() {
  const target = typeof route.query.redirect === 'string' ? route.query.redirect : '/home'
  router.push(target)
}

async function doLogin() {
  errorMsg.value = ''
  loading.value = true
  try {
    await auth.login(login.value.email, login.value.password)
    redirectAfter()
  } catch (e) {
    errorMsg.value = apiMessage(e, '로그인에 실패했어요.')
  } finally {
    loading.value = false
  }
}

async function doRegister() {
  errorMsg.value = ''
  loading.value = true
  try {
    await auth.register(register.value.email, register.value.password, register.value.nickname)
    redirectAfter()
  } catch (e) {
    errorMsg.value = apiMessage(e, '회원가입에 실패했어요.')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" sm="8" md="5">
        <p class="eyebrow text-primary mb-3">환영합니다</p>
        <h1 class="display-xl mb-8">함께 시작해요.</h1>

        <v-card class="pa-6 pa-sm-8">
          <v-tabs v-model="tab" color="primary" class="mb-6" @update:model-value="errorMsg = ''">
            <v-tab value="login">로그인</v-tab>
            <v-tab value="register">회원가입</v-tab>
          </v-tabs>

          <v-alert v-if="errorMsg" type="error" variant="tonal" density="comfortable" class="mb-4">
            {{ errorMsg }}
          </v-alert>

          <v-window v-model="tab">
            <v-window-item value="login">
              <v-form @submit.prevent="doLogin">
                <v-text-field v-model="login.email" label="이메일" :rules="emailRules" class="mb-2" />
                <v-text-field v-model="login.password" label="비밀번호" type="password" :rules="pwRules" class="mb-4" />
                <v-btn type="submit" block size="large" color="primary" variant="flat" :loading="loading">
                  로그인
                </v-btn>
              </v-form>
            </v-window-item>

            <v-window-item value="register">
              <v-form @submit.prevent="doRegister">
                <v-text-field v-model="register.email" label="이메일" :rules="emailRules" class="mb-2" />
                <v-text-field v-model="register.nickname" label="닉네임" :rules="nickRules" class="mb-2" />
                <v-text-field v-model="register.password" label="비밀번호 (8자 이상)" type="password" :rules="pwRules" class="mb-4" />
                <v-btn type="submit" block size="large" color="primary" variant="flat" :loading="loading">
                  회원가입
                </v-btn>
              </v-form>
            </v-window-item>
          </v-window>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
