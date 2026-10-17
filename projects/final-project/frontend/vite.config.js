import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  // 빌드 결과를 Spring Boot의 static/app 아래에 넣어 /app/ 으로 제공하므로 상대 경로로 만든다.
  base: './',
  server: {
    // 개발 서버(5173)에서 /api 요청을 백엔드(8080)로 넘긴다. 브라우저에는 같은 출처로 보여 CORS 설정이 필요 없다.
    proxy: { '/api': 'http://localhost:8080' },
  },
  test: {
    environment: 'jsdom',
    setupFiles: './src/test/setup.js',
    globals: true,
  },
})
