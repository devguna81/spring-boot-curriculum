import { Link } from 'react-router'

export default function NotFoundPage() {
  return (
    <main>
      <h1>찾을 수 없습니다</h1>
      <p>
        <Link to="/users">목록으로</Link>
      </p>
    </main>
  )
}
