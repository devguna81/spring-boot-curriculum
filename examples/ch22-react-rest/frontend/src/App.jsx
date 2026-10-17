import { Link, Navigate, Route, Routes } from 'react-router'
import UserListPage from './pages/UserListPage.jsx'
import UserFormPage from './pages/UserFormPage.jsx'

export default function App() {
  return (
    <>
      <nav>
        <Link to="/users">사용자</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Navigate to="/users" replace />} />
        <Route path="/users" element={<UserListPage />} />
        <Route path="/users/new" element={<UserFormPage />} />
        <Route path="/users/:id/edit" element={<UserFormPage />} />
        <Route path="*" element={<main><h1>찾을 수 없습니다</h1></main>} />
      </Routes>
    </>
  )
}
