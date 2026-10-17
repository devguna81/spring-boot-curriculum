import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router'
import App from './App.jsx'
import { encodeBasic, login, logout } from './auth/session.js'
import { installFakeFetch } from './test/fakeServer.js'

beforeEach(() => logout())
afterEach(() => vi.unstubAllGlobals())

const list = { items: [], page: 0, size: 10, total: 0 }

describe('App', () => {
  it('로그인하지 않았으면 로그인/가입 링크를 보여 준다', async () => {
    installFakeFetch(() => ({ body: list }))
    render(<MemoryRouter initialEntries={['/']}><App /></MemoryRouter>)
    expect(await screen.findByText('글 목록', { selector: 'h1' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: '로그인' })).toBeInTheDocument()
    expect(screen.queryByRole('link', { name: '글쓰기' })).toBeNull()
  })

  it('로그인 상태면 내 정보를 읽어 이름과 글쓰기 링크를 보여 준다', async () => {
    login('alice', 'secret-1234')
    const { calls } = installFakeFetch((_m, url) => ({
      body: url.endsWith('/users/me') ? { id: 10, username: 'alice', displayName: '앨리스', role: 'USER' } : list,
    }))
    render(<MemoryRouter initialEntries={['/posts']}><App /></MemoryRouter>)
    expect(await screen.findByText('앨리스(alice)')).toBeInTheDocument()
    expect(screen.getByRole('link', { name: '글쓰기' })).toBeInTheDocument()
    expect(calls.find((c) => c.url.endsWith('/users/me')).headers).toEqual({ Authorization: encodeBasic('alice', 'secret-1234') })
  })

  it('저장된 ID가 잘못됐으면 로그아웃 상태로 돌아간다', async () => {
    login('ghost', 'x')
    installFakeFetch((_m, url) => (url.endsWith('/users/me')
      ? { status: 401, body: { code: 'UNAUTHORIZED', message: 'x', issues: [] } }
      : { body: list }))
    render(<MemoryRouter initialEntries={['/posts']}><App /></MemoryRouter>)
    expect(await screen.findByRole('link', { name: '로그인' })).toBeInTheDocument()
  })

  it('로그아웃하면 로그인 링크가 돌아온다', async () => {
    login('alice', 'secret-1234')
    installFakeFetch((_m, url) => ({
      body: url.endsWith('/users/me') ? { id: 10, username: 'alice', displayName: '앨리스', role: 'USER' } : list,
    }))
    render(<MemoryRouter initialEntries={['/posts']}><App /></MemoryRouter>)
    await userEvent.click(await screen.findByRole('button', { name: '로그아웃' }))
    expect(await screen.findByRole('link', { name: '로그인' })).toBeInTheDocument()
  })

  it('없는 경로는 안내를 보여 준다', () => {
    installFakeFetch(() => ({ body: list }))
    render(<MemoryRouter initialEntries={['/nowhere']}><App /></MemoryRouter>)
    expect(screen.getByText('찾을 수 없습니다')).toBeInTheDocument()
  })
})
