import { useEffect, useMemo, useState } from 'react'
import UserTable from '../components/UserTable.jsx'
import { users } from '../data/users.js'

export default function UserListPage() {
  // 상태(state): 값이 바뀌면 이 컴포넌트가 다시 그려진다.
  const [activeOnly, setActiveOnly] = useState(false)
  const [keyword, setKeyword] = useState('')

  // 부수 효과(effect): 렌더링 결과를 화면에 반영한 뒤 화면 밖의 것(문서 제목)을 맞춘다.
  useEffect(() => {
    document.title = '사용자 목록 - ch21'
  }, [])

  // 파생 값: 상태에서 계산할 수 있는 값은 따로 상태로 두지 않고 계산한다.
  const visibleUsers = useMemo(
    () =>
      users.filter(
        (user) =>
          (!activeOnly || user.active) &&
          user.name.toLowerCase().includes(keyword.trim().toLowerCase()),
      ),
    [activeOnly, keyword],
  )

  return (
    <main>
      <h1>사용자 목록</h1>
      <div className="controls">
        <label>
          <input
            type="checkbox"
            checked={activeOnly}
            onChange={(event) => setActiveOnly(event.target.checked)}
          />
          활성만 보기
        </label>
        <label>
          이름 검색
          <input value={keyword} onChange={(event) => setKeyword(event.target.value)} />
        </label>
      </div>
      <UserTable users={visibleUsers} />
      <p>
        총 <span data-testid="count">{visibleUsers.length}</span>명
      </p>
    </main>
  )
}
