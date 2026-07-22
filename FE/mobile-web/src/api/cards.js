import { clients } from './http'

// 카드 조회 (content 서비스)
export const cardsApi = {
  list: (params) => clients.content.get('/api/v1/cards', { params }),
  detail: (slug) => clients.content.get(`/api/v1/cards/${slug}`),
}
