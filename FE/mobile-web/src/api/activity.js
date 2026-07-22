import { clients } from './http'

// 사용자 활동 (user-activity 서비스): 좋아요/북마크, 댓글, 신고, 내 서재
export const reactionsApi = {
  toggle: (cardId, type) => clients.activity.post(`/api/v1/cards/${cardId}/reactions`, { type }),
}

export const commentsApi = {
  list: (cardId, params) => clients.activity.get(`/api/v1/cards/${cardId}/comments`, { params }),
  create: (cardId, content) => clients.activity.post(`/api/v1/cards/${cardId}/comments`, { content }),
  report: (commentId) => clients.activity.post(`/api/v1/comments/${commentId}/report`),
}

export const libraryApi = {
  bookmarks: (userId, params) => clients.activity.get(`/api/v1/users/${userId}/bookmarks`, { params }),
}
