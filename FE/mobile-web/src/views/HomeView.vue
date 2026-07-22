<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 랜딩 히어로 + 플랫폼 소개. 실제 카드 피드는 Phase 3 에서 이 아래에 연동된다.
const pillars = [
  {
    icon: 'mdi-cards-outline',
    title: '큐레이션 카드',
    body: 'AI 레포·스킬을 4축 실전점수로 평가한 카드로 한눈에.',
    to: '/dashboard',
  },
  {
    icon: 'mdi-comment-question-outline',
    title: 'Q&A',
    body: '막히는 지점을 묻고, 아는 것을 나눕니다.',
    to: '/qna',
  },
  {
    icon: 'mdi-account-group-outline',
    title: '프로젝트 협업',
    body: '아이디어를 팀으로 만들어 결과물로.',
    to: '/projects',
  },
  {
    icon: 'mdi-trophy-outline',
    title: '주간 랭킹',
    body: '이번 주 가장 주목받은 AI를 매주 월요일에.',
    to: '/rankings/weekly',
  },
]

const stats = [
  { value: '4축', label: '실전점수 평가' },
  { value: '매주', label: '갱신되는 랭킹' },
  { value: '함께', label: '만드는 큐레이션' },
]
</script>

<template>
  <div>
    <!-- HERO -->
    <section class="hero-glow">
      <v-container class="container-max section pb-0">
        <v-row>
          <v-col cols="12" md="10" lg="9">
            <p class="eyebrow text-primary mb-4">AI 지식 공유 커뮤니티</p>
            <h1 class="display-hero text-balance mb-6">
              지금의 AI를<br />
              <span class="text-primary">한눈에</span>, 함께.
            </h1>
            <p class="lead text-medium-emphasis text-balance mb-8" style="max-width: 42rem">
              쏟아지는 AI 레포와 스킬을 커뮤니티가 직접 큐레이션합니다.
              실전점수로 고르고, 질문으로 배우고, 프로젝트로 만듭니다.
            </p>
            <div class="d-flex flex-wrap ga-3">
              <v-btn size="x-large" color="primary" variant="flat" @click="router.push('/dashboard')">
                카드 둘러보기
                <v-icon end>mdi-arrow-right</v-icon>
              </v-btn>
              <v-btn size="x-large" variant="outlined" @click="router.push('/login')">
                시작하기
              </v-btn>
            </div>
          </v-col>
        </v-row>

        <v-row class="mt-12">
          <v-col v-for="s in stats" :key="s.label" cols="4" md="3">
            <div class="display-lg text-primary">{{ s.value }}</div>
            <div class="text-body-2 text-medium-emphasis mt-1">{{ s.label }}</div>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <!-- PILLARS -->
    <section>
      <v-container class="container-max section">
        <v-row>
          <v-col cols="12" md="6">
            <h2 class="display-xl text-balance mb-4">
              흩어진 정보를,<br />쓸모 있는 지식으로.
            </h2>
          </v-col>
          <v-col cols="12" md="6" class="d-flex align-end">
            <p class="lead text-medium-emphasis">
              네 가지 방식으로 커뮤니티에 참여하세요. 읽고, 반응하고, 묻고, 만듭니다.
            </p>
          </v-col>
        </v-row>

        <v-row class="mt-6">
          <v-col v-for="p in pillars" :key="p.title" cols="12" sm="6" lg="3">
            <v-card class="pa-6 h-100 card-hover" @click="router.push(p.to)">
              <v-icon :icon="p.icon" size="34" color="primary" class="mb-4" />
              <h3 class="display-md mb-2">{{ p.title }}</h3>
              <p class="text-body-2 text-medium-emphasis">{{ p.body }}</p>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <!-- CTA -->
    <section>
      <v-container class="container-max section pt-0">
        <v-card class="pa-8 pa-md-12 text-center" color="surface-variant">
          <h2 class="display-xl text-balance mb-4">오늘의 AI, 함께 골라볼까요?</h2>
          <p class="lead text-medium-emphasis mb-8">
            무료로 시작하고, 관심 있는 카드에 좋아요·북마크를 남겨보세요.
          </p>
          <v-btn size="x-large" color="primary" variant="flat" @click="router.push('/login')">
            회원가입 / 로그인
          </v-btn>
        </v-card>
      </v-container>
    </section>
  </div>
</template>
