import { useState } from 'react'
import { useNavigate } from 'react-router'
import { usersApi } from '../api/usersApi.js'
import { login, logout } from '../auth/session.js'

export default function LoginPage({ onChange }) {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)
    // 서버에는 로그인 API가 따로 없다. 자격 증명을 넣고 내 정보를 읽어 보며 맞는지 확인한다.
    login(form.username, form.password)
    try {
      await usersApi.me()
      await onChange()
      navigate('/posts')
    } catch (e) {
      logout() // 틀린 자격 증명이 남아 다른 요청에 붙지 않게 지운다.
      setError(e.status === 401 ? '아이디 또는 비밀번호가 올바르지 않습니다.' : e.message)
      setSubmitting(false)
    }
  }

  return (
    <main>
      <h1>로그인</h1>
      <form onSubmit={submit}>
        {error && <p className="error" role="alert">{error}</p>}
        <label htmlFor="username">아이디</label>
        <input id="username" type="text" autoComplete="username" value={form.username}
               onChange={(e) => setForm({ ...form, username: e.target.value })} />
        <label htmlFor="password">비밀번호</label>
        <input id="password" type="password" autoComplete="current-password" value={form.password}
               onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <p><button type="submit" disabled={submitting}>로그인</button></p>
      </form>
    </main>
  )
}
