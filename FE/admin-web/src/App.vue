<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useTheme } from 'vuetify'
import { useAuthStore } from '@/stores/auth'
import { errorBus } from '@/api/http'

const router = useRouter()
const auth = useAuthStore()
const theme = useTheme()

const snackbar = ref({ show: false, text: '' })
errorBus.handler = (text) => {
  snackbar.value = { show: true, text }
}

const isDark = computed(() => theme.global.current.value.dark)
function toggleTheme() {
  theme.global.name.value = isDark.value ? 'light' : 'dark'
}

const navLinks = [
  { title: '검수함', to: '/admin/submissions', icon: 'mdi-inbox-arrow-down-outline' },
  { title: '카드 관리', to: '/admin/cards', icon: 'mdi-cards-outline' },
]

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <v-app>
    <v-navigation-drawer permanent width="248" class="border-e">
      <div class="pa-5 d-flex align-center">
        <span class="brand-dot mr-2" />
        <div>
          <div class="font-display font-weight-bold">AI 커뮤니티</div>
          <div class="text-caption text-medium-emphasis">큐레이터 콘솔</div>
        </div>
      </div>
      <v-divider />
      <v-list nav density="comfortable" class="mt-2">
        <v-list-item prepend-icon="mdi-view-dashboard-outline" title="대시보드" to="/dashboard" />
        <v-list-item
          v-for="link in navLinks"
          :key="link.to"
          :prepend-icon="link.icon"
          :title="link.title"
          :to="link.to"
        />
      </v-list>
    </v-navigation-drawer>

    <v-app-bar flat height="64" class="border-b">
      <v-container class="d-flex align-center py-0" fluid>
        <v-spacer />
        <v-btn :icon="isDark ? 'mdi-weather-sunny' : 'mdi-weather-night'" variant="text" @click="toggleTheme" />
        <template v-if="auth.isAuthenticated">
          <span class="text-body-2 text-medium-emphasis mr-3">{{ auth.nickname }}</span>
          <v-btn variant="tonal" rounded="lg" @click="logout">로그아웃</v-btn>
        </template>
        <v-btn v-else color="primary" variant="flat" rounded="lg" to="/login">로그인</v-btn>
      </v-container>
    </v-app-bar>

    <v-main>
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </v-main>

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
.border-e {
  border-inline-end: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
}
</style>
