import { useState } from 'react'
import { useNavigate } from 'react-router'
import { ApiError } from '../api/client.js'
import { usersApi } from '../api/usersApi.js'
import { login } from '../auth/session.js'

const FIELDS = [
  ['username', '아이디 (영소문자, 숫자, _ / 3~30자)', 'text'],
  ['password', '비밀번호 (8~72자)', 'password'],
  ['displayName', '표시 이름', 'text'],
]

export default function SignupPage({ onChange }) {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '', displayName: '' })
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setSubmitting(true)
    setFieldErrors({})
    setFormError(null)
    try {
      await usersApi.signup(form)
      login(form.username, form.password) // 가입 직후 바로 로그인 상태로 만든다.
      await onChange()
      navigate('/posts')
    } catch (e) {
      if (e instanceof ApiError && Object.keys(e.fieldErrors()).length > 0) {
        setFieldErrors(e.fieldErrors())
      } else {
        setFormError(e.message)
      }
      setSubmitting(false)
    }
  }

  return (
    <main>
      <h1>가입</h1>
      <form onSubmit={submit} noValidate>
        {formError && <p className="error" role="alert">{formError}</p>}
        {FIELDS.map(([name, label, type]) => (
          <div key={name}>
            <label htmlFor={name}>{label}</label>
            <input id={name} name={name} type={type} value={form[name]}
                   onChange={(e) => setForm({ ...form, [name]: e.target.value })} />
            {fieldErrors[name] && <span className="error">{fieldErrors[name]}</span>}
          </div>
        ))}
        <p><button type="submit" disabled={submitting}>가입</button></p>
      </form>
    </main>
  )
}
