import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'

// 관리자 웹은 서비스별 base URL 로 직접 호출한다 (src/api/services.js).
// 각 백엔드는 CORS 로 dev origin(5174)을 허용한다.
export default defineConfig({
  plugins: [vue(), vuetify({ autoImport: true })],
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
  },
  server: {
    port: 5174,
  },
})
