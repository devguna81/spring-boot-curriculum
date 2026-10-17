import { useCallback, useEffect, useState } from 'react'
import { Link, Navigate, Route, Routes, useNavigate } from 'react-router'
import { usersApi } from './api/usersApi.js'
import { getUserId, logout } from './auth/session.js'
import LoginPage from './pages/LoginPage.jsx'
import PostDetailPage from './pages/PostDetailPage.jsx'
import PostFormPage from './pages/PostFormPage.jsx'
import PostListPage from './pages/PostListPage.jsx'
import SignupPage from './pages/SignupPage.jsx'

export default function App() {
  const navigate = useNavigate()
  const [me, setMe] = useState(null)

  // 로그인 상태가 바뀔 때마다 서버에서 내 정보를 다시 읽는다. 저장된 ID가 잘못됐으면 로그아웃 처리한다.
  const refreshMe = useCallback(async () => {
    if (!getUserId()) {
      setMe(null)
      return
    }
    try {
      setMe(await usersApi.me())
    } catch {
      logout()
      setMe(null)
    }
  }, [])

  useEffect(() => {
    refreshMe()
  }, [refreshMe])

  function signOut() {
    logout()
    setMe(null)
    navigate('/posts')
  }

  return (
    <>
      <nav>
        <Link to="/posts">글 목록</Link>
        {me ? (
          <>
            <Link to="/posts/new">글쓰기</Link>
            <span>{me.displayName}({me.username})</span>{' '}
            <button type="button" onClick={signOut}>로그아웃</button>
          </>
        ) : (
          <>
            <Link to="/login">로그인</Link>
            <Link to="/signup">가입</Link>
          </>
        )}
        <a href="/pages/posts">서버 렌더링 화면</a>
      </nav>
      <Routes>
        <Route path="/" element={<Navigate to="/posts" replace />} />
        <Route path="/posts" element={<PostListPage />} />
        <Route path="/posts/new" element={<PostFormPage />} />
        <Route path="/posts/:id" element={<PostDetailPage me={me} />} />
        <Route path="/posts/:id/edit" element={<PostFormPage />} />
        <Route path="/login" element={<LoginPage onChange={refreshMe} />} />
        <Route path="/signup" element={<SignupPage onChange={refreshMe} />} />
        <Route path="*" element={<main><h1>찾을 수 없습니다</h1></main>} />
      </Routes>
    </>
  )
}
