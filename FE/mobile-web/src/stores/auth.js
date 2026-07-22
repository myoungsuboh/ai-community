import { defineStore } from 'pinia'
import { clients, bindAuthStore } from '@/api/http'
import { authApi } from '@/api/auth'

// 사용자 컨텍스트 상태.
// 액세스 토큰은 메모리에만 보관(영속 저장 금지 — routing-auth 규칙),
// 리프레시 토큰은 서버가 HttpOnly 쿠키로 관리. 새로고침 시 silent refresh 로 세션 복원.
export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: null,
    user: null, // { id, email, nickname, role }
    ready: false, // 최초 silent refresh 완료 여부
  }),
  getters: {
    isAuthenticated: (s) => !!s.accessToken,
    isCurator: (s) => s.user?.role === 'CURATOR' || s.user?.role === 'ADMIN',
    nickname: (s) => s.user?.nickname || '',
  },
  actions: {
    setSession(data) {
      this.accessToken = data.accessToken
      this.user = data.user
    },
    async restore() {
      bindAuthStore(this)
      // 페이지 로드 시 HttpOnly 쿠키로 조용히 재발급 시도 (실패는 정상 = 비로그인)
      await this.tryRefresh()
      this.ready = true
    },
    async register(email, password, nickname) {
      const data = await authApi.register(email, password, nickname)
      this.setSession(data)
      return data.user
    },
    async login(email, password) {
      const data = await authApi.login(email, password)
      this.setSession(data)
      return data.user
    },
    // http 401 인터셉터 및 restore 가 호출. 성공 시 true.
    async tryRefresh() {
      try {
        const data = await clients.auth.post('/api/v1/auth/refresh')
        this.setSession(data)
        return true
      } catch {
        this.accessToken = null
        this.user = null
        return false
      }
    },
    async logout() {
      try {
        await authApi.logout()
      } catch {
        // 서버 실패해도 로컬 세션은 정리
      }
      this.accessToken = null
      this.user = null
    },
    clear() {
      this.accessToken = null
      this.user = null
    },
  },
})
