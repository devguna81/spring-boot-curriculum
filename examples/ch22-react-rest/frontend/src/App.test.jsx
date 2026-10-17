import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router'
import { afterEach, describe, expect, it, vi } from 'vitest'
import App from './App.jsx'
import { installFakeFetch } from './test/fakeServer.js'

afterEach(() => vi.unstubAllGlobals())

const KIM = { id: 1, name: 'kim', email: 'kim@example.com', age: 28 }
const LEE = { id: 2, name: 'lee', email: 'lee@example.com', age: 35 }

function renderAt(path) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <App />
    </MemoryRouter>,
  )
}

describe('목록', () => {
  it('불러오는 중 문구를 보여 준 뒤 목록을 그린다', async () => {
    installFakeFetch(() => ({ body: [KIM, LEE] }))
    renderAt('/users')
    expect(screen.getByText('불러오는 중...')).toBeInTheDocument()
    expect(await screen.findByText('kim@example.com')).toBeInTheDocument()
    expect(screen.getAllByRole('row')).toHaveLength(3)
  })

  it('루트 경로는 목록으로 이동한다', async () => {
    installFakeFetch(() => ({ body: [] }))
    renderAt('/')
    expect(await screen.findByText('등록된 사용자가 없습니다.')).toBeInTheDocument()
  })

  it('불러오기에 실패하면 오류와 다시 시도를 보여 주고, 다시 시도하면 목록을 그린다', async () => {
    let attempt = 0
    installFakeFetch(() => (++attempt === 1 ? { status: 500 } : { body: [KIM] }))
    const user = userEvent.setup()
    renderAt('/users')
    expect(await screen.findByRole('alert')).toHaveTextContent('목록을 불러오지 못했습니다')
    await user.click(screen.getByRole('button', { name: '다시 시도' }))
    expect(await screen.findByText('kim@example.com')).toBeInTheDocument()
  })

  it('서버가 보낸 이름의 태그를 HTML 로 해석하지 않는다', async () => {
    installFakeFetch(() => ({ body: [{ ...KIM, name: '<b>kim</b>' }] }))
    const { container } = renderAt('/users')
    expect(await screen.findByText('<b>kim</b>')).toBeInTheDocument()
    expect(container.querySelector('b')).toBeNull()
  })

  it('삭제하면 DELETE 를 보내고 목록을 다시 불러온다', async () => {
    let users = [KIM, LEE]
    const { calls } = installFakeFetch((method, url) => {
      if (method === 'DELETE') {
        users = users.filter((u) => `/api/users/${u.id}` !== url)
        return { status: 204 }
      }
      return { body: users }
    })
    const user = userEvent.setup()
    renderAt('/users')
    await screen.findByText('kim@example.com')
    await user.click(screen.getAllByRole('button', { name: '삭제' })[0])
    await waitFor(() => expect(screen.queryByText('kim@example.com')).not.toBeInTheDocument())
    expect(calls.map((c) => c.method)).toEqual(['GET', 'DELETE', 'GET'])
    expect(calls[1].url).toBe('/api/users/1')
  })

  it('삭제에 실패하면 오류를 보여 주고 목록은 그대로 둔다', async () => {
    installFakeFetch((method) =>
      method === 'DELETE'
        ? { status: 404, body: { code: 'USER_NOT_FOUND', message: '사용자를 찾을 수 없습니다. id=1', issues: [] } }
        : { body: [KIM] },
    )
    const user = userEvent.setup()
    renderAt('/users')
    await screen.findByText('kim@example.com')
    await user.click(screen.getByRole('button', { name: '삭제' }))
    expect(await screen.findByRole('alert')).toHaveTextContent('사용자를 찾을 수 없습니다. id=1')
    expect(screen.getByText('kim@example.com')).toBeInTheDocument()
  })
})

describe('등록', () => {
  it('입력값을 POST 로 보내고 목록으로 돌아가 안내를 보여 준다', async () => {
    const { calls } = installFakeFetch((method) =>
      method === 'POST' ? { status: 201, body: { id: 3, name: 'new', email: 'new@example.com', age: 20 } } : { body: [KIM] },
    )
    const user = userEvent.setup()
    renderAt('/users/new')
    await user.type(screen.getByLabelText('이름'), 'new')
    await user.type(screen.getByLabelText('이메일'), 'new@example.com')
    await user.type(screen.getByLabelText('나이'), '20')
    await user.click(screen.getByRole('button', { name: '저장' }))
    expect(await screen.findByText('등록했습니다: new')).toBeInTheDocument()
    const post = calls.find((c) => c.method === 'POST')
    expect(post.body).toEqual({ name: 'new', email: 'new@example.com', age: 20 }) // 나이는 숫자로
  })

  it('빈 나이는 null 로 보내고 서버의 검증 오류를 입력 칸 옆에 보여 준다', async () => {
    const { calls } = installFakeFetch(() => ({
      status: 400,
      body: {
        code: 'VALIDATION_FAILED',
        message: '요청 값이 올바르지 않습니다',
        issues: [
          { field: 'age', reason: '나이는 필수입니다' },
          { field: 'name', reason: '이름은 2자 이상 20자 이하여야 합니다' },
        ],
      },
    }))
    const user = userEvent.setup()
    renderAt('/users/new')
    await user.type(screen.getByLabelText('이름'), 'k')
    await user.click(screen.getByRole('button', { name: '저장' }))
    expect(await screen.findByText('나이는 필수입니다')).toBeInTheDocument()
    expect(screen.getByText('이름은 2자 이상 20자 이하여야 합니다')).toBeInTheDocument()
    expect(calls[0].body.age).toBeNull()
    // 오류가 나도 입력한 값은 남아 있고 폼은 다시 제출할 수 있다.
    expect(screen.getByLabelText('이름')).toHaveValue('k')
    expect(screen.getByRole('button', { name: '저장' })).toBeEnabled()
  })

  it('중복 이메일(409)을 이메일 칸 옆에 보여 준다', async () => {
    installFakeFetch(() => ({
      status: 409,
      body: { code: 'DUPLICATE_EMAIL', message: '이미 사용 중인 이메일입니다', issues: [{ field: 'email', reason: '이미 사용 중인 이메일입니다' }] },
    }))
    const user = userEvent.setup()
    renderAt('/users/new')
    await user.type(screen.getByLabelText('이름'), 'dup')
    await user.type(screen.getByLabelText('이메일'), 'kim@example.com')
    await user.type(screen.getByLabelText('나이'), '20')
    await user.click(screen.getByRole('button', { name: '저장' }))
    expect(await screen.findByText('이미 사용 중인 이메일입니다')).toBeInTheDocument()
  })

  it('필드와 무관한 오류(서버 오류)는 폼 위쪽에 보여 준다', async () => {
    installFakeFetch(() => ({ status: 500 }))
    const user = userEvent.setup()
    renderAt('/users/new')
    await user.type(screen.getByLabelText('이름'), 'abc')
    await user.click(screen.getByRole('button', { name: '저장' }))
    expect(await screen.findByRole('alert')).toHaveTextContent('요청이 실패했습니다 (500)')
  })
})

describe('수정', () => {
  it('기존 값을 채워 보여 주고 PUT 으로 저장한다', async () => {
    const { calls } = installFakeFetch((method, url) => {
      if (method === 'PUT') return { body: { ...KIM, name: 'kim2' } }
      if (url === '/api/users/1') return { body: KIM }
      return { body: [KIM] }
    })
    const user = userEvent.setup()
    renderAt('/users/1/edit')
    const name = await screen.findByLabelText('이름')
    expect(name).toHaveValue('kim')
    expect(screen.getByLabelText('나이')).toHaveValue('28')
    await user.clear(name)
    await user.type(name, 'kim2')
    await user.click(screen.getByRole('button', { name: '저장' }))
    expect(await screen.findByText('수정했습니다: kim2')).toBeInTheDocument()
    const put = calls.find((c) => c.method === 'PUT')
    expect(put.url).toBe('/api/users/1')
    expect(put.body).toEqual({ name: 'kim2', email: 'kim@example.com', age: 28 })
  })

  it('없는 사용자를 열면 오류를 보여 준다', async () => {
    installFakeFetch(() => ({
      status: 404,
      body: { code: 'USER_NOT_FOUND', message: '사용자를 찾을 수 없습니다. id=999', issues: [] },
    }))
    renderAt('/users/999/edit')
    expect(await screen.findByRole('alert')).toHaveTextContent('사용자를 찾을 수 없습니다. id=999')
  })
})
