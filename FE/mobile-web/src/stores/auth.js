import { defineStore } from 'pinia'
import { clients, bindAuthStore } from '@/api/http'

const STORAGE_KEY = 'aic.auth'

// 사용자 컨텍스트 상태. 로그인/회원가입 API 연동은 Phase 2 에서 완성.
export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: null,
    refreshToken: null,
    user: null, // { id, email, nickname, role }
  }),
  getters: {
    isAuthenticated: (s) => !!s.accessToken,
    isCurator: (s) => s.user?.role === 'CURATOR' || s.user?.role === 'ADMIN',
    nickname: (s) => s.user?.nickname || '',
  },
  actions: {
    restore() {
      bindAuthStore(this)
      try {
        const raw = localStorage.getItem(STORAGE_KEY)
        if (raw) {
          const parsed = JSON.parse(raw)
          this.accessToken = parsed.accessToken || null
          this.refreshToken = parsed.refreshToken || null
          this.user = parsed.user || null
        }
      } catch {
        this.clear()
      }
    },
    persist() {
      localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({
          accessToken: this.accessToken,
          refreshToken: this.refreshToken,
          user: this.user,
        }),
      )
    },
    setSession({ accessToken, refreshToken, user }) {
      this.accessToken = accessToken
      this.refreshToken = refreshToken
      this.user = user
      this.persist()
    },
    // 401 시 http 인터셉터가 1회 호출. 성공하면 true.
    async tryRefresh() {
      if (!this.refreshToken) return false
      try {
        const data = await clients.auth.post('/api/v1/auth/refresh', {
          refreshToken: this.refreshToken,
        })
        this.accessToken = data.accessToken
        this.refreshToken = data.refreshToken || this.refreshToken
        this.persist()
        return true
      } catch {
        return false
      }
    },
    clear() {
      this.accessToken = null
      this.refreshToken = null
      this.user = null
      localStorage.removeItem(STORAGE_KEY)
    },
    logout() {
      this.clear()
    },
  },
})
