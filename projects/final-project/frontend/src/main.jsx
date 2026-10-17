import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { HashRouter } from 'react-router'
import App from './App.jsx'
import './styles.css'

// 정적 파일로 제공하므로 새로고침해도 서버가 경로를 몰라 404가 나지 않도록 해시(#) 주소를 쓴다.
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <HashRouter>
      <App />
    </HashRouter>
  </StrictMode>,
)
