import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router'
import { postsApi } from '../api/postsApi.js'

export default function PostListPage() {
  // 검색 조건은 주소(?keyword=..&tag=..&page=..)에 둔다. 새로고침하거나 주소를 공유해도 같은 결과가 나온다.
  const [params, setParams] = useSearchParams()
  const keyword = params.get('keyword') ?? ''
  const tag = params.get('tag') ?? ''
  const page = Number(params.get('page') ?? 0)

  const [form, setForm] = useState({ keyword, tag })
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false
    setError(null)
    postsApi
      .search({ keyword, tag, page })
      .then((data) => {
        if (!cancelled) setResult(data)
      })
      .catch((e) => {
        if (!cancelled) setError(e)
      })
    return () => {
      cancelled = true
    }
  }, [keyword, tag, page])

  function search(event) {
    event.preventDefault()
    const next = {}
    if (form.keyword.trim()) next.keyword = form.keyword.trim()
    if (form.tag.trim()) next.tag = form.tag.trim()
    setParams(next) // 조건이 바뀌면 첫 페이지부터 본다.
  }

  function goto(target) {
    const next = Object.fromEntries(params)
    next.page = String(target)
    setParams(next)
  }

  const totalPages = result ? Math.ceil(result.total / result.size) : 0

  return (
    <main>
      <h1>글 목록</h1>
      <form className="search" onSubmit={search}>
        <label>검색어
          <input type="text" value={form.keyword} onChange={(e) => setForm({ ...form, keyword: e.target.value })} />
        </label>
        <label>태그
          <input type="text" value={form.tag} onChange={(e) => setForm({ ...form, tag: e.target.value })} />
        </label>
        <button type="submit">검색</button>
      </form>

      {error && <p className="error" role="alert">{error.message}</p>}
      {!result && !error && <p>불러오는 중...</p>}
      {result && (
        <>
          <p>전체 {result.total}건</p>
          {result.items.length === 0 ? (
            <p>조건에 맞는 글이 없습니다.</p>
          ) : (
            <table>
              <thead>
                <tr><th>제목</th><th>작성자</th><th>태그</th><th>작성일</th></tr>
              </thead>
              <tbody>
                {result.items.map((post) => (
                  <tr key={post.id} className={post.pinned ? 'pinned' : undefined}>
                    <td>{post.pinned && <strong>[고정] </strong>}<Link to={`/posts/${post.id}`}>{post.title}</Link></td>
                    <td>{post.authorName}</td>
                    <td>{post.tags.map((t) => <span key={t} className="tag">{t}</span>)}</td>
                    <td>{post.createdAt.slice(0, 16).replace('T', ' ')}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
          {totalPages > 1 && (
            <p>
              <button type="button" disabled={page <= 0} onClick={() => goto(page - 1)}>이전</button>{' '}
              {page + 1} / {totalPages}{' '}
              <button type="button" disabled={page + 1 >= totalPages} onClick={() => goto(page + 1)}>다음</button>
            </p>
          )}
        </>
      )}
    </main>
  )
}
