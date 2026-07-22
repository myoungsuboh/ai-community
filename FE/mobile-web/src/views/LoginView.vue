<script setup>
import { ref } from 'vue'

// Phase 1: 화면/폼 골격 + 클라이언트 검증. 실제 로그인/회원가입 API 연동은 Phase 2.
const tab = ref('login')
const info = ref(false)

const login = ref({ email: '', password: '' })
const register = ref({ email: '', password: '', nickname: '' })

const emailRules = [(v) => !!v || '이메일을 입력해 주세요.', (v) => /.+@.+\..+/.test(v) || '올바른 이메일 형식이 아니에요.']
const pwRules = [(v) => !!v || '비밀번호를 입력해 주세요.', (v) => (v || '').length >= 8 || '비밀번호는 8자 이상이에요.']
const nickRules = [(v) => !!v || '닉네임을 입력해 주세요.']

function submit() {
  // Phase 2 에서 auth-service 실제 API(POST /api/v1/auth/login · /register)로 교체됩니다.
  info.value = true
}
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" sm="8" md="5">
        <p class="eyebrow text-primary mb-3">환영합니다</p>
        <h1 class="display-xl mb-8">함께 시작해요.</h1>

        <v-card class="pa-6 pa-sm-8">
          <v-tabs v-model="tab" color="primary" class="mb-6">
            <v-tab value="login">로그인</v-tab>
            <v-tab value="register">회원가입</v-tab>
          </v-tabs>

          <v-window v-model="tab">
            <v-window-item value="login">
              <v-form @submit.prevent="submit">
                <v-text-field v-model="login.email" label="이메일" :rules="emailRules" class="mb-2" />
                <v-text-field v-model="login.password" label="비밀번호" type="password" :rules="pwRules" class="mb-4" />
                <v-btn type="submit" block size="large" color="primary" variant="flat">로그인</v-btn>
              </v-form>
            </v-window-item>

            <v-window-item value="register">
              <v-form @submit.prevent="submit">
                <v-text-field v-model="register.email" label="이메일" :rules="emailRules" class="mb-2" />
                <v-text-field v-model="register.nickname" label="닉네임" :rules="nickRules" class="mb-2" />
                <v-text-field v-model="register.password" label="비밀번호 (8자 이상)" type="password" :rules="pwRules" class="mb-4" />
                <v-btn type="submit" block size="large" color="primary" variant="flat">회원가입</v-btn>
              </v-form>
            </v-window-item>
          </v-window>
        </v-card>

        <v-alert v-if="info" type="info" variant="tonal" class="mt-4" density="comfortable">
          로그인/회원가입 기능은 Phase 2에서 백엔드와 연결됩니다.
        </v-alert>
      </v-col>
    </v-row>
  </v-container>
</template>
