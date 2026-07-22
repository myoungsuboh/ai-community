import { test, expect } from '@playwright/test'

// 주요 사용자 흐름 E2E: 회원가입 → 게시글 작성 → 목록에서 확인 (FE → BE → DB).
// 전제: 백엔드 auth(8081)·curation(8083) 실행 중.
test('회원가입하고 게시글을 작성하면 목록에 보인다', async ({ page }) => {
  const email = `e2e${Date.now()}@ai.community`
  const title = `E2E 테스트 게시글 ${Date.now()}`

  // 회원가입
  await page.goto('/login')
  await page.getByRole('tab', { name: '회원가입' }).click()
  await page.getByLabel('이메일').fill(email)
  await page.getByLabel('닉네임').fill('E2E러너')
  await page.getByLabel(/비밀번호/).fill('e2epassword1')
  await page.getByRole('button', { name: '회원가입' }).click()

  // 홈으로 리다이렉트 (로그인 상태)
  await expect(page).toHaveURL(/\/home/)

  // 게시글 작성
  await page.goto('/posts/new')
  await page.getByLabel(/제목/).fill(title)
  await page.getByLabel(/내용/).fill('E2E 테스트로 작성한 게시글 본문입니다. 충분히 깁니다.')
  await page.getByRole('button', { name: '발행' }).click()

  // 상세로 이동 후 목록에서 확인
  await page.goto('/posts')
  await expect(page.getByText(title)).toBeVisible()
})
