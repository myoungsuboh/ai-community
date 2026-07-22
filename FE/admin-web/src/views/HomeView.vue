<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const actions = [
  {
    icon: 'mdi-inbox-arrow-down-outline',
    title: '제보 검수함',
    body: '회원이 제보한 URL을 검토하고 요약·점수를 확정해 발행하거나 반려합니다.',
    to: '/admin/submissions',
  },
  {
    icon: 'mdi-cards-outline',
    title: '카드 관리',
    body: '발행된 AI 레포/스킬 카드의 정보를 최신 상태로 유지합니다.',
    to: '/admin/cards',
  },
]
</script>

<template>
  <v-container class="container-max section">
    <p class="eyebrow text-primary mb-3">큐레이터 콘솔</p>
    <h1 class="display-xl mb-3">
      안녕하세요{{ auth.nickname ? ', ' + auth.nickname : '' }} 👋
    </h1>
    <p class="lead text-medium-emphasis mb-10" style="max-width: 40rem">
      커뮤니티의 지식 품질을 지키는 곳입니다. 오늘의 검수 대기 건을 확인해 보세요.
    </p>

    <v-row>
      <v-col v-for="a in actions" :key="a.to" cols="12" md="6">
        <v-card class="pa-6 h-100 card-hover" @click="router.push(a.to)">
          <v-icon :icon="a.icon" size="34" color="primary" class="mb-4" />
          <h3 class="display-md mb-2">{{ a.title }}</h3>
          <p class="text-body-2 text-medium-emphasis mb-4">{{ a.body }}</p>
          <v-btn variant="tonal" rounded="lg">바로가기 <v-icon end>mdi-arrow-right</v-icon></v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
