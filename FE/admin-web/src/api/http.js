import axios from 'axios'
import { SERVICE_BASE } from './services'

// ── HTTP 클라이언트 표준 (skills/frontEnd/http-client-api-standard.md) ──
// 서비스별 base URL 이 다르므로(마이크로서비스), 동일한 인터셉터 로직을 공유하는
// 클라이언트를 팩토리로 생성한다. 토큰 자동 첨부 · 401 1회 갱신 후 재시도 · 전역 에러.

let authStoreRef = null
// 순환 의존을 피하기 위해 런타임에 auth store 를 주입한다.
export function bindAuthStore(store) {
  authStoreRef = store
}

// 전역 에러(네트워크/5xx) 알림 훅. main/App 에서 토스트로 연결.
export const errorBus = {
  handler: (message) => console.error('[api]', message),
  emit(message) {
    this.handler(message)
  },
}

function createClient(baseURL) {
  const client = axios.create({
    baseURL,
    timeout: 15000,
    withCredentials: true, // HttpOnly refresh 쿠키 송수신
    headers: { 'Content-Type': 'application/json' },
  })

  // 요청: 토큰 자동 첨부
  client.interceptors.request.use((config) => {
    const token = authStoreRef?.accessToken
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  // 응답: 성공 시 data 언랩 / 401 1회 갱신 후 재시도 / 5xx·네트워크 전역 알림
  client.interceptors.response.use(
    (response) => response.data,
    async (error) => {
      const original = error.config || {}
      const status = error.response?.status
      const isAuthEndpoint = (original.url || '').includes('/api/v1/auth/')

      if (status === 401 && authStoreRef && !original._retried && !isAuthEndpoint) {
        original._retried = true
        const refreshed = await authStoreRef.tryRefresh()
        if (refreshed) {
          original.headers = original.headers || {}
          original.headers.Authorization = `Bearer ${authStoreRef.accessToken}`
          return client(original)
        }
        authStoreRef.clear()
      }

      if (!error.response) {
        errorBus.emit('네트워크 연결을 확인해 주세요.')
      } else if (status >= 500) {
        errorBus.emit('일시적인 서버 오류가 발생했어요. 잠시 후 다시 시도해 주세요.')
      }
      return Promise.reject(error)
    },
  )

  return client
}

// 서비스별 공유 클라이언트
export const clients = Object.fromEntries(
  Object.entries(SERVICE_BASE).map(([name, baseURL]) => [name, createClient(baseURL)]),
)

// 편의: 서버 에러 응답의 사용자 메시지 추출
export function apiMessage(error, fallback = '요청을 처리하지 못했어요.') {
  return error?.response?.data?.message || fallback
}
