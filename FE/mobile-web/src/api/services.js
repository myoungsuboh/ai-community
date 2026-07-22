// 논리 서비스명 → base URL. dev 기본값은 각 백엔드 마이크로서비스 포트.
// 운영에서는 VITE_* 환경변수로 실제 게이트웨이/호스트를 주입한다.
const env = import.meta.env

export const SERVICE_BASE = {
  auth: env.VITE_AUTH_URL || 'http://localhost:8081',
  content: env.VITE_CONTENT_URL || 'http://localhost:8082',
  curation: env.VITE_CURATION_URL || 'http://localhost:8083',
  activity: env.VITE_ACTIVITY_URL || 'http://localhost:8084',
  qna: env.VITE_QNA_URL || 'http://localhost:8085',
  project: env.VITE_PROJECT_URL || 'http://localhost:8086',
  ranking: env.VITE_RANKING_URL || 'http://localhost:8087',
}
