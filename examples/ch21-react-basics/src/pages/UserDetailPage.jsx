import { useEffect } from 'react'
import { Link, useParams } from 'react-router'
import { users } from '../data/users.js'
import NotFoundPage from './NotFoundPage.jsx'

export default function UserDetailPage() {
  // 경로의 :id 는 문자열로 온다. 숫자 id 와 비교하려면 변환해야 한다.
  const { id } = useParams()
  const user = users.find((candidate) => String(candidate.id) === id)

  useEffect(() => {
    document.title = user ? `${user.name} - ch21` : '찾을 수 없음 - ch21'
  }, [user])

  if (!user) {
    return <NotFoundPage />
  }
  return (
    <main>
      <h1>사용자 상세</h1>
      <dl>
        <dt>이름</dt>
        <dd>{user.name}</dd>
        <dt>이메일</dt>
        <dd>{user.email}</dd>
        <dt>나이</dt>
        <dd>{user.age}</dd>
        <dt>상태</dt>
        <dd>{user.active ? '활성' : '비활성'}</dd>
      </dl>
      <p>
        <Link to="/users">목록으로</Link>
      </p>
    </main>
  )
}
