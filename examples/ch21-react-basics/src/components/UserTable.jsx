import UserRow from './UserRow.jsx'

export default function UserTable({ users }) {
  if (users.length === 0) {
    return <p>표시할 사용자가 없습니다.</p>
  }
  return (
    <table>
      <thead>
        <tr>
          <th>번호</th>
          <th>이름</th>
          <th>이메일</th>
          <th>나이</th>
          <th>상태</th>
        </tr>
      </thead>
      <tbody>
        {/* 목록을 그릴 때는 항목마다 안정적인 key 가 필요하다. */}
        {users.map((user) => (
          <UserRow key={user.id} user={user} />
        ))}
      </tbody>
    </table>
  )
}
