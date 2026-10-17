import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    // 브라우저가 없는 Node 에서 DOM 을 흉내 내는 jsdom 환경으로 컴포넌트를 렌더링한다.
    environment: 'jsdom',
    setupFiles: './src/test/setup.js',
    // 테스트가 끝나면 렌더링한 DOM 을 정리하려고 전역 afterEach 를 쓴다.
    globals: true,
  },
})
