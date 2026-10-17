import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router'
import LoginPage from './LoginPage.jsx'
import { isLoggedIn, logout } from '../auth/session.js'
import { installFakeFetch } from '../test/fakeServer.js'

beforeEach(() => logout())
afterEach(() => vi.unstubAllGlobals())

function renderLogin(onChange = vi.fn()) {
  render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<LoginPage onChange={onChange} />} />
        <Route path="/posts" element={<p>목록 화면</p>} />
      </Routes>
    </MemoryRouter>,
  )
  return onChange
}

async function fill(username, password) {
  await userEvent.type(screen.getByLabelText('아이디'), username)
  await userEvent.type(screen.getByLabelText('비밀번호'), password)
  await userEvent.click(screen.getByRole('button', { name: '로그인' }))
}

describe('LoginPage', () => {
  it('맞는 자격 증명이면 로그인하고 목록으로 이동한다', async () => {
    const { calls } = installFakeFetch(() => ({ body: { id: 1, username: 'alice', displayName: '앨리스', role: 'USER' } }))
    const onChange = renderLogin()
    await fill('alice', 'secret-1234')

    expect(await screen.findByText('목록 화면')).toBeInTheDocument()
    expect(isLoggedIn()).toBe(true)
    expect(calls[0].url).toBe('/api/v1/users/me')
    expect(calls[0].headers.Authorization).toMatch(/^Basic /)
    expect(onChange).toHaveBeenCalled()
  })

  it('틀린 자격 증명이면 안내하고 저장하지 않는다', async () => {
    installFakeFetch(() => ({ status: 401, body: { code: 'UNAUTHORIZED', message: 'x', issues: [] } }))
    renderLogin()
    await fill('alice', 'wrong-password')

    expect(await screen.findByRole('alert')).toHaveTextContent('아이디 또는 비밀번호가 올바르지 않습니다.')
    expect(isLoggedIn()).toBe(false)
    expect(screen.getByRole('button', { name: '로그인' })).toBeEnabled()
  })
})
