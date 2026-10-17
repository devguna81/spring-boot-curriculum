import { Link } from 'react-router'

// props 로 받은 값만 사용해 한 행을 그린다. 상태가 없는 순수한 컴포넌트다.
export default function UserRow({ user }) {
  return (
    <tr className={user.active ? undefined : 'inactive'}>
      <td>{user.id}</td>
      <td>
        <Link to={`/users/${user.id}`}>{user.name}</Link>
      </td>
      <td>{user.email}</td>
      <td>{user.age}</td>
      <td>{user.active ? '활성' : '비활성'}</td>
    </tr>
  )
}
