import { useCallback, useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router'
import { usersApi } from '../api/usersApi.js'

export default function UserListPage() {
  const [users, setUsers] = useState(null) // null 이면 아직 불러오지 못한 상태
  const [error, setError] = useState(null)
  const [actionError, setActionError] = useState(null)
  // 폼 화면에서 저장한 뒤 navigate 로 넘겨 준 안내. 새로고침하면 사라진다.
  const { state } = useLocation()
  const message = state?.message

  const load = useCallback(async (isCancelled = () => false) => {
    setError(null)
    try {
      const result = await usersApi.list()
      if (!isCancelled()) setUsers(result)
    } catch (e) {
      if (!isCancelled()) setError(e)
    }
  }, [])

  useEffect(() => {
    // 화면이 사라진 뒤(또는 StrictMode 의 첫 실행 뒤) 도착한 응답으로 상태를 바꾸지 않게 한다.
    let cancelled = false
    load(() => cancelled)
    return () => {
      cancelled = true
    }
  }, [load])

  async function remove(user) {
    setActionError(null)
    try {
      await usersApi.remove(user.id)
      await load()
    } catch (e) {
      setActionError(e)
    }
  }

  return (
    <main>
      <h1>사용자 목록</h1>
      {message && <p className="message">{message}</p>}
      <p>
        <Link to="/users/new">새 사용자</Link>
      </p>
      {actionError && <p className="error" role="alert">{actionError.message}</p>}
      {error ? (
        <p className="error" role="alert">
          목록을 불러오지 못했습니다: {error.message}{' '}
          <button onClick={() => load()}>다시 시도</button>
        </p>
      ) : users === null ? (
        <p>불러오는 중...</p>
      ) : users.length === 0 ? (
        <p>등록된 사용자가 없습니다.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>번호</th>
              <th>이름</th>
              <th>이메일</th>
              <th>나이</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>{user.age}</td>
                <td>
                  <Link to={`/users/${user.id}/edit`}>수정</Link>{' '}
                  <button onClick={() => remove(user)}>삭제</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  )
}
