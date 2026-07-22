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

  { path: '/posts', name: 'posts', component: () => import('@/views/PostsView.vue') },
  { path: '/posts/new', name: 'post-new', meta: { requiresAuth: true }, component: () => import('@/views/PostCreateView.vue') },
  { path: '/posts/:postId', name: 'post-detail', component: () => import('@/views/PostDetailView.vue') },

  { path: '/cards/:cardSlug', name: 'card-detail', component: () => import('@/views/CardDetailView.vue') },
  { path: '/submit', name: 'submit', meta: { requiresAuth: true }, component: () => import('@/views/SubmitView.vue') },

  { path: '/my-library', name: 'my-library', meta: { requiresAuth: true }, component: () => import('@/views/MyLibraryView.vue') },
  { path: '/qna', name: 'qna', component: () => import('@/views/QnaView.vue') },

  { path: '/projects', name: 'projects', component: () => import('@/views/ProjectsView.vue') },
  { path: '/projects/new', name: 'project-new', meta: { requiresAuth: true }, component: () => import('@/views/ProjectCreateView.vue') },
  { path: '/projects/:projectId', name: 'project-detail', component: () => import('@/views/ProjectDetailView.vue') },

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
// 판단 전에 세션 복원(silent refresh)이 끝나도록 보장한다 — 새로고침 시 오판 방지.
router.beforeEach(async (to) => {
  const auth = useAuthStore()
  await auth.ensureReady()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
