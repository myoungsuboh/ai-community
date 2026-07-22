import { clients } from './http'

// 주간 랭킹 (ranking-api 서비스)
export const rankingApi = {
  weekly: () => clients.ranking.get('/api/v1/rankings/weekly'),
}
