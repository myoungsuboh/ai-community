import { clients } from './http'

// URL 제보 (curation 서비스)
export const submissionsApi = {
  create: (url) => clients.curation.post('/api/v1/submissions', { url }),
}
