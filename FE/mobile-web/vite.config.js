import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'

// FE 는 서비스별 base URL 로 직접 호출한다 (src/api/services.js).
// 카드 리소스가 content/user-activity/curation 세 서비스로 나뉘어 단일 프록시로는
// 구분이 어렵기 때문. 각 서비스는 CORS 로 dev origin(5173/5174)을 허용한다.
export default defineConfig({
  plugins: [vue(), vuetify({ autoImport: true })],
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
  },
  server: {
    port: 5173,
  },
})
