<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 사용자 프로필. 명세에 공개 사용자 조회 API 가 없어, 본인 프로필은 세션 정보로 표시하고
// 타인 프로필은 최소 정보만 표시한다.
const route = useRoute()
const auth = useAuthStore()
const userId = computed(() => route.params.userId)
const isMe = computed(() => auth.user?.id === userId.value)

const roleLabel = { MEMBER: '회원', CURATOR: '큐레이터', ADMIN: '관리자' }
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" md="7">
        <v-card class="pa-8 text-center">
          <v-avatar size="80" color="primary" class="mb-4">
            <span v-if="isMe" class="display-md text-white">{{ auth.nickname.slice(0, 1) }}</span>
            <v-icon v-else icon="mdi-account" size="40" color="white" />
          </v-avatar>

          <template v-if="isMe">
            <h1 class="display-lg mb-1">{{ auth.nickname }}</h1>
            <v-chip size="small" variant="tonal" color="primary" class="mb-4">
              {{ roleLabel[auth.user?.role] || auth.user?.role }}
            </v-chip>
            <p class="text-medium-emphasis mb-6">{{ auth.user?.email }}</p>
            <v-btn color="primary" variant="flat" to="/my-library">내 서재 보기</v-btn>
          </template>

          <template v-else>
            <h1 class="display-lg mb-2">커뮤니티 사용자</h1>
            <p class="text-medium-emphasis text-caption">ID: {{ userId }}</p>
            <p class="text-medium-emphasis mt-4">공개 프로필 정보는 아직 제공되지 않아요.</p>
          </template>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
