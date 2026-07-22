import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 화면 라우팅 (IMPLEMENTATION-CHECKLIST Screens 기준).
// 아직 구현 전인 화면은 PlaceholderView 로 연결하고 담당 Phase 를 표기한다.
const placeholder = (title, phase) => ({
  component: () => import('@/views/PlaceholderView.vue'),
  props: { title, phase },
})

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', name: 'home', component: () => import('@/views/HomeView.vue') },

  { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },

  { path: '/dashboard', name: 'dashboard', ...placeholder('메인 대시보드', 'Phase 3+') },
  { path: '/models', name: 'models', ...placeholder('AI 모델/코드 공유', 'Phase 3') },

  { path: '/posts', name: 'posts', ...placeholder('게시글 목록', 'Phase 3') },
  { path: '/posts/new', name: 'post-new', meta: { requiresAuth: true }, ...placeholder('게시글 작성', 'Phase 3') },
  { path: '/posts/:postId', name: 'post-detail', ...placeholder('게시글 상세', 'Phase 3') },

  { path: '/cards/:cardSlug', name: 'card-detail', ...placeholder('카드 상세', 'Phase 3') },
  { path: '/submit', name: 'submit', meta: { requiresAuth: true }, ...placeholder('제보하기', 'Phase 3') },

  { path: '/my-library', name: 'my-library', meta: { requiresAuth: true }, ...placeholder('내 서재', 'Phase 4') },
  { path: '/qna', name: 'qna', ...placeholder('Q&A 목록', 'Phase 5') },

  { path: '/projects', name: 'projects', ...placeholder('프로젝트 목록', 'Phase 6') },
  { path: '/projects/new', name: 'project-new', meta: { requiresAuth: true }, ...placeholder('프로젝트 생성', 'Phase 6') },
  { path: '/projects/:projectId', name: 'project-detail', ...placeholder('프로젝트 상세', 'Phase 6') },

  { path: '/rankings/weekly', name: 'rankings', ...placeholder('주간 랭킹', 'Phase 7') },
  { path: '/users/:userId', name: 'user-profile', ...placeholder('사용자 프로필', 'Phase 3+') },

  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFoundView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

// 인증 가드 (skills/frontEnd/routing-routing-auth.md)
router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
