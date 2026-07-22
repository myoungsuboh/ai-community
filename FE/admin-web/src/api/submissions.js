import { clients } from './http'

// 제보 검수 (curation 서비스)
export const submissionsApi = {
  list: (params) => clients.curation.get('/api/v1/submissions', { params }),
  review: (id, payload) => clients.curation.patch(`/api/v1/submissions/${id}/review`, payload),
}
