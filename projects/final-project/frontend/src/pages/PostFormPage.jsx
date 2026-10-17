import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { ApiError } from '../api/client.js'
import { postsApi } from '../api/postsApi.js'

const EMPTY = { title: '', content: '', tags: '', pinned: false }

/** "a, b ,," 같은 입력을 ["a", "b"] 로 바꾼다. */
export function parseTags(text) {
  return text.split(',').map((t) => t.trim()).filter((t) => t !== '')
}

export default function PostFormPage() {
  const { id } = useParams() // 있으면 수정, 없으면 작성
  const navigate = useNavigate()
  const [form, setForm] = useState(EMPTY)
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [loading, setLoading] = useState(Boolean(id))
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!id) return undefined
    let cancelled = false
    postsApi
      .get(id)
      .then((post) => {
        if (!cancelled) {
          setForm({ title: post.title, content: post.content, tags: post.tags.join(', '), pinned: post.pinned })
        }
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

  async function submit(event) {
    event.preventDefault()
    setSubmitting(true)
    setFieldErrors({})
    setFormError(null)
    const payload = { title: form.title, content: form.content, tags: parseTags(form.tags), pinned: form.pinned }
    try {
      const saved = id ? await postsApi.update(id, payload) : await postsApi.create(payload)
      navigate(`/posts/${saved.id}`)
    } catch (e) {
      if (e instanceof ApiError && Object.keys(e.fieldErrors()).length > 0) {
        setFieldErrors(e.fieldErrors())
      } else {
        setFormError(e.status === 401 ? '로그인이 필요합니다.' : e.message)
      }
      setSubmitting(false)
    }
  }

  if (loading) {
    return <main><p>불러오는 중...</p></main>
  }
  return (
    <main>
      <h1>{id ? '글 수정' : '글쓰기'}</h1>
      <form onSubmit={submit} noValidate>
        {formError && <p className="error" role="alert">{formError}</p>}
        <label htmlFor="title">제목</label>
        <input id="title" type="text" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
        {fieldErrors.title && <span className="error">{fieldErrors.title}</span>}

        <label htmlFor="content">본문</label>
        <textarea id="content" value={form.content} onChange={(e) => setForm({ ...form, content: e.target.value })} />
        {fieldErrors.content && <span className="error">{fieldErrors.content}</span>}

        <label htmlFor="tags">태그 (쉼표로 구분, 최대 5개)</label>
        <input id="tags" type="text" value={form.tags} onChange={(e) => setForm({ ...form, tags: e.target.value })} />
        {Object.entries(fieldErrors).filter(([f]) => f.startsWith('tags')).map(([f, r]) => (
          <span key={f} className="error">{r}</span>
        ))}

        <label>
          <input type="checkbox" checked={form.pinned} onChange={(e) => setForm({ ...form, pinned: e.target.checked })} />
          {' '}상단에 고정
        </label>

        <p>
          <button type="submit" disabled={submitting}>저장</button>{' '}
          <Link to={id ? `/posts/${id}` : '/posts'}>취소</Link>
        </p>
      </form>
    </main>
  )
}
