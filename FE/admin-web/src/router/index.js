import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const placeholder = (title, phase) => ({
  component: () => import('@/views/PlaceholderView.vue'),
  props: { title, phase },
})

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', name: 'dashboard', component: () => import('@/views/HomeView.vue'), meta: { requiresAuth: true, requiresCurator: true } },
  { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },

  { path: '/admin/submissions', name: 'submissions', meta: { requiresAuth: true, requiresCurator: true }, component: () => import('@/views/SubmissionsView.vue') },
  { path: '/admin/cards', name: 'cards-admin', meta: { requiresAuth: true, requiresCurator: true }, ...placeholder('카드 관리', 'Phase 3') },

  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFoundView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

// 인증 + 큐레이터 권한 가드. 판단 전에 세션 복원 완료를 보장한다.
router.beforeEach(async (to) => {
  const auth = useAuthStore()
  await auth.ensureReady()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresCurator && !auth.isCurator) {
    return { name: 'login', query: { redirect: to.fullPath, denied: '1' } }
  }
  return true
})

export default router
