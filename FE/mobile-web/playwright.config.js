import { defineConfig, devices } from '@playwright/test'

// E2E 설정 (skills/testing/playwright-e2e-testing.md).
// 전제: 백엔드(auth 8081, content 8082, curation 8083, user-activity 8084)가 실행 중이어야 한다.
// webServer 로 프론트 dev 서버를 자동 기동한다.
export default defineConfig({
  testDir: './e2e',
  timeout: 30_000,
  fullyParallel: false,
  retries: 0,
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: true,
    timeout: 60_000,
  },
})
