import { useState } from 'react'
import { useNavigate } from 'react-router'
import { login } from '../auth/session.js'

// 서버가 아직 임시 인증(사용자 ID 헤더)이라 ID만 받는다. f04에서 아이디/비밀번호 입력으로 바뀐다.
export default function LoginPage({ onChange }) {
  const navigate = useNavigate()
  const [userId, setUserId] = useState('')

  async function submit(event) {
    event.preventDefault()
    if (userId.trim() === '') return
    login(userId.trim())
    await onChange()
    navigate('/posts')
  }

  return (
    <main>
      <h1>로그인</h1>
      <form onSubmit={submit}>
        <label htmlFor="userId">사용자 ID</label>
        <input id="userId" type="text" value={userId} onChange={(e) => setUserId(e.target.value)} />
        <p><button type="submit">로그인</button></p>
      </form>
    </main>
  )
}
