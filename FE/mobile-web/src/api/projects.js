import { clients } from './http'

// 프로젝트 (project 서비스)
export const projectsApi = {
  list: (params) => clients.project.get('/api/v1/projects', { params }),
  get: (id) => clients.project.get(`/api/v1/projects/${id}`),
  create: (payload) => clients.project.post('/api/v1/projects', payload),
  update: (id, payload) => clients.project.put(`/api/v1/projects/${id}`, payload),
  updateStatus: (id, payload) => clients.project.patch(`/api/v1/projects/${id}/status`, payload),
}

export const PROJECT_STATUS = {
  RECRUITING: '팀원 모집 중',
  IN_PROGRESS: '진행 중',
  DONE: '완료',
}
