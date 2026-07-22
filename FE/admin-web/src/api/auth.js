import { clients } from './http'

// 인증 API (auth-service). refreshToken 은 HttpOnly 쿠키로 오가므로 body 에 없다.
export const authApi = {
  register: (email, password, nickname) =>
    clients.auth.post('/api/v1/auth/register', { email, password, nickname }),

  login: (email, password) =>
    clients.auth.post('/api/v1/auth/login', { email, password }),

  refresh: () => clients.auth.post('/api/v1/auth/refresh'),

  logout: () => clients.auth.post('/api/v1/auth/logout'),
}
