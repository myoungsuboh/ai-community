<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useTheme } from 'vuetify'
import { useAuthStore } from '@/stores/auth'
import { errorBus } from '@/api/http'

const router = useRouter()
const auth = useAuthStore()
const theme = useTheme()

const drawer = ref(false)
const snackbar = ref({ show: false, text: '' })

// 전역 에러(네트워크/5xx)를 토스트로 노출
errorBus.handler = (text) => {
  snackbar.value = { show: true, text }
}

const isDark = computed(() => theme.global.current.value.dark)
function toggleTheme() {
  theme.global.name.value = isDark.value ? 'light' : 'dark'
}

const navLinks = [
  { title: '홈', to: '/home' },
  { title: '카드', to: '/dashboard' },
  { title: '게시글', to: '/posts' },
  { title: 'Q&A', to: '/qna' },
  { title: '프로젝트', to: '/projects' },
  { title: '랭킹', to: '/rankings/weekly' },
]

function goLogin() {
  router.push({ name: 'login' })
}
function logout() {
  auth.logout()
  router.push('/home')
}
</script>

<template>
  <v-app>
    <v-app-bar flat height="72" class="border-b">
      <v-container class="container-max d-flex align-center py-0">
        <router-link to="/home" class="d-flex align-center text-decoration-none">
          <span class="brand-dot mr-2" />
          <span class="font-display text-h6 font-weight-bold">AI 커뮤니티</span>
        </router-link>

        <div class="d-none d-md-flex align-center ml-8">
          <v-btn
            v-for="link in navLinks"
            :key="link.to"
            :to="link.to"
            variant="text"
            class="text-body-2 font-weight-medium px-3"
          >
            {{ link.title }}
          </v-btn>
        </div>

        <v-spacer />

        <v-btn :icon="isDark ? 'mdi-weather-sunny' : 'mdi-weather-night'" variant="text" @click="toggleTheme" />

        <template v-if="auth.isAuthenticated">
          <v-btn variant="text" class="d-none d-sm-flex" to="/my-library">내 서재</v-btn>
          <v-menu>
            <template #activator="{ props }">
              <v-btn v-bind="props" variant="tonal" rounded="lg" class="ml-1">
                {{ auth.nickname || '내 계정' }}
              </v-btn>
            </template>
            <v-list>
              <v-list-item title="내 서재" to="/my-library" />
              <v-list-item title="로그아웃" @click="logout" />
            </v-list>
          </v-menu>
        </template>
        <v-btn v-else color="primary" variant="flat" rounded="lg" class="ml-1" @click="goLogin">
          로그인
        </v-btn>

        <v-app-bar-nav-icon class="d-md-none ml-1" @click="drawer = !drawer" />
      </v-container>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" temporary location="right">
      <v-list>
        <v-list-item v-for="link in navLinks" :key="link.to" :title="link.title" :to="link.to" />
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </v-main>

    <v-footer class="border-t py-8">
      <v-container class="container-max">
        <div class="d-flex flex-column flex-md-row justify-space-between align-md-center ga-2">
          <span class="font-display font-weight-bold">AI 커뮤니티</span>
          <span class="text-body-2 text-medium-emphasis">
            지금의 AI를 함께 큐레이션합니다 · © 2026
          </span>
        </div>
      </v-container>
    </v-footer>

    <v-snackbar v-model="snackbar.show" color="error" timeout="4000" location="top">
      {{ snackbar.text }}
    </v-snackbar>
  </v-app>
</template>

<style scoped>
.brand-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgb(var(--v-theme-primary)), rgb(var(--v-theme-secondary)));
  display: inline-block;
}
.border-b {
  border-bottom: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
}
.border-t {
  border-top: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
}
</style>
