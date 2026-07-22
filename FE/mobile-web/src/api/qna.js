import { clients } from './http'

// Q&A (qna 서비스)
export const qnaApi = {
  list: (params) => clients.qna.get('/api/v1/questions', { params }),
  detail: (id) => clients.qna.get(`/api/v1/questions/${id}`),
  createQuestion: (payload) => clients.qna.post('/api/v1/questions', payload),
  createAnswer: (questionId, content) =>
    clients.qna.post(`/api/v1/questions/${questionId}/answers`, { content }),
}
