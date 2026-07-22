import { defineStore } from 'pinia'
import { clients, bindAuthStore } from '@/api/http'
import { authApi } from '@/api/auth'

// 최초 세션 복원(silent refresh)은 앱 전체에서 한 번만 수행하도록 모듈 레벨에 메모이즈한다.
let restorePromise = null

// 사용자 컨텍스트 상태.
// 액세스 토큰은 메모리에만(영속 저장 금지 — routing-auth 규칙), 리프레시는 서버 HttpOnly 쿠키.
export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: null,
    user: null, // { id, email, nickname, role }
    ready: false,
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
    // 가드/부트스트랩이 호출. 최초 1회만 refresh 하고 이후엔 완료된 프라미스를 반환.
    ensureReady() {
      if (!restorePromise) {
        restorePromise = this._restore()
      }
      return restorePromise
    },
    async _restore() {
      bindAuthStore(this)
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
