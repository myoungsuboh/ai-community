import { clients } from './http'

// 게시글 (curation 서비스)
export const postsApi = {
  list: (params) => clients.curation.get('/api/v1/posts', { params }),
  get: (id) => clients.curation.get(`/api/v1/posts/${id}`),
  create: (payload) => clients.curation.post('/api/v1/posts', payload),
}
