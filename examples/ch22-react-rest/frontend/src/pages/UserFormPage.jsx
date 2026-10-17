import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { ApiError, usersApi } from '../api/usersApi.js'

const EMPTY = { name: '', email: '', age: '' }

// 서버의 issues([{ field, reason }])를 { field: reason } 으로 바꿔 입력 칸 옆에 보여 준다.
function toFieldErrors(error) {
  const result = {}
  for (const issue of error.body?.issues ?? []) {
    result[issue.field] = issue.reason
  }
  return result
}

export default function UserFormPage() {
  const { id } = useParams() // 있으면 수정, 없으면 등록
  const navigate = useNavigate()
  const [form, setForm] = useState(EMPTY)
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [loading, setLoading] = useState(Boolean(id))
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!id) return undefined
    let cancelled = false
    usersApi
      .get(id)
      .then((user) => {
        if (!cancelled) setForm({ name: user.name, email: user.email, age: String(user.age) })
      })
      .catch((e) => {
        if (!cancelled) setFormError(e.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [id])

  function change(event) {
    setForm({ ...form, [event.target.name]: event.target.value })
  }

  async function submit(event) {
    event.preventDefault() // 브라우저의 기본 폼 전송(페이지 이동)을 막고 fetch 로 보낸다.
    setSubmitting(true)
    setFieldErrors({})
    setFormError(null)
    // 입력 칸의 값은 항상 문자열이다. 빈 칸은 null 로 보내 서버가 "필수" 오류를 돌려주게 한다.
    const payload = { ...form, age: form.age === '' ? null : Number(form.age) }
    try {
      const saved = id ? await usersApi.update(id, payload) : await usersApi.create(payload)
      navigate('/users', { state: { message: `${id ? '수정' : '등록'}했습니다: ${saved.name}` } })
    } catch (e) {
      if (e instanceof ApiError && Object.keys(toFieldErrors(e)).length > 0) {
        setFieldErrors(toFieldErrors(e))
      } else {
        setFormError(e.message)
      }
      setSubmitting(false)
    }
  }

  if (loading) {
    return <main><p>불러오는 중...</p></main>
  }
  return (
    <main>
      <h1>{id ? '사용자 수정' : '사용자 등록'}</h1>
      <form onSubmit={submit} noValidate>
        {formError && <p className="error" role="alert">{formError}</p>}
        {['name', 'email', 'age'].map((field) => (
          <div key={field}>
            <label>
              {{ name: '이름', email: '이메일', age: '나이' }[field]}
              <input name={field} value={form[field]} onChange={change} />
            </label>
            {fieldErrors[field] && <span className="error">{fieldErrors[field]}</span>}
          </div>
        ))}
        <button type="submit" disabled={submitting}>저장</button>{' '}
        <Link to="/users">취소</Link>
      </form>
    </main>
  )
}
