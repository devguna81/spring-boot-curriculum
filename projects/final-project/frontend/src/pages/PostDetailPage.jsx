import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { postsApi } from '../api/postsApi.js'

export default function PostDetailPage({ me }) {
  const { id } = useParams()
  const navigate = useNavigate()
  const [post, setPost] = useState(null)
  const [error, setError] = useState(null)
  const [actionError, setActionError] = useState(null)

  useEffect(() => {
    let cancelled = false
    postsApi
      .get(id)
      .then((data) => {
        if (!cancelled) setPost(data)
      })
      .catch((e) => {
        if (!cancelled) setError(e)
      })
    return () => {
      cancelled = true
    }
  }, [id])

  async function remove() {
    if (!window.confirm('이 글을 삭제할까요?')) return
    try {
      await postsApi.remove(id)
      navigate('/posts')
    } catch (e) {
      setActionError(e)
    }
  }

  if (error) {
    return <main><p className="error" role="alert">{error.status === 404 ? '글을 찾을 수 없습니다.' : error.message}</p></main>
  }
  if (!post) {
    return <main><p>불러오는 중...</p></main>
  }
  // 버튼은 편의를 위해 숨길 뿐이다. 실제 권한 검사는 서버가 한다.
  const canEdit = me && (me.role === 'ADMIN' || me.id === post.authorId)
  return (
    <main>
      <h1>{post.title}</h1>
      <p>{post.authorName} · {post.createdAt.slice(0, 16).replace('T', ' ')}{post.pinned && ' · 고정'}</p>
      <p>{post.tags.map((t) => <span key={t} className="tag">{t}</span>)}</p>
      {/* React 는 텍스트를 이스케이프해서 넣는다. HTML 로 해석하지 않으므로 줄바꿈만 CSS 로 살린다. */}
      <div className="content">{post.content}</div>
      {actionError && <p className="error" role="alert">{actionError.message}</p>}
      {canEdit && (
        <p>
          <Link to={`/posts/${post.id}/edit`}>수정</Link>{' '}
          <button type="button" onClick={remove}>삭제</button>
        </p>
      )}
      <p><Link to="/posts">목록으로</Link></p>
    </main>
  )
}
