import { Link, Route, Routes } from 'react-router'
import UserListPage from './pages/UserListPage.jsx'
import UserDetailPage from './pages/UserDetailPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

// 경로와 화면(컴포넌트)을 연결한다. 라우터(BrowserRouter)는 main.jsx 에서 감싼다.
export default function App() {
  return (
    <>
      <nav>
        <Link to="/users">사용자</Link>
      </nav>
      <Routes>
        <Route path="/" element={<UserListPage />} />
        <Route path="/users" element={<UserListPage />} />
        <Route path="/users/:id" element={<UserDetailPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </>
  )
}
