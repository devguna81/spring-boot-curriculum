import { afterEach, describe, expect, it, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router'
import PostFormPage, { parseTags } from './PostFormPage.jsx'
import { installFakeFetch } from '../test/fakeServer.js'

afterEach(() => vi.unstubAllGlobals())

function renderForm(path = '/posts/new') {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/posts/new" element={<PostFormPage />} />
        <Route path="/posts/:id/edit" element={<PostFormPage />} />
        <Route path="/posts/:id" element={<p>상세 화면</p>} />
      </Routes>
    </MemoryRouter>,
  )
}

describe('parseTags', () => {
  it('쉼표로 나누고 공백과 빈 항목을 버린다', () => {
    expect(parseTags('a, b ,, c')).toEqual(['a', 'b', 'c'])
    expect(parseTags('')).toEqual([])
  })
})

describe('PostFormPage', () => {
  it('입력값을 계약 모양으로 보내고 상세 화면으로 이동한다', async () => {
    const { calls } = installFakeFetch(() => ({ status: 201, body: { id: 5 } }))
    renderForm()
    await userEvent.type(screen.getByLabelText('제목'), '새 글')
    await userEvent.type(screen.getByLabelText('본문'), '내용')
    await userEvent.type(screen.getByLabelText(/태그/), 'a, b')
    await userEvent.click(screen.getByLabelText(/상단에 고정/))
    await userEvent.click(screen.getByRole('button', { name: '저장' }))

    expect(await screen.findByText('상세 화면')).toBeInTheDocument()
    expect(calls[0]).toMatchObject({
      method: 'POST', url: '/api/v1/posts',
      body: { title: '새 글', content: '내용', tags: ['a', 'b'], pinned: true },
    })
  })

  it('서버의 필드별 검증 오류를 입력 칸 옆에 보여 준다', async () => {
    installFakeFetch(() => ({
      status: 400,
      body: { code: 'VALIDATION_FAILED', message: 'x', issues: [{ field: 'title', reason: '제목은 필수입니다' }, { field: 'tags[0]', reason: '태그가 깁니다' }] },
    }))
    renderForm()
    await userEvent.click(screen.getByRole('button', { name: '저장' }))
    expect(await screen.findByText('제목은 필수입니다')).toBeInTheDocument()
    expect(screen.getByText('태그가 깁니다')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: '저장' })).toBeEnabled()
  })

  it('로그인하지 않았을 때의 401 은 안내 문구로 바꿔 보여 준다', async () => {
    installFakeFetch(() => ({ status: 401, body: { code: 'UNAUTHORIZED', message: 'x', issues: [] } }))
    renderForm()
    await userEvent.click(screen.getByRole('button', { name: '저장' }))
    expect(await screen.findByRole('alert')).toHaveTextContent('로그인이 필요합니다.')
  })

  it('수정 화면은 기존 값을 채우고 PUT 으로 보낸다', async () => {
    const { calls } = installFakeFetch((method) => (method === 'GET'
      ? { body: { id: 4, title: '기존', content: '본문', tags: ['x', 'y'], pinned: true } }
      : { body: { id: 4 } }))
    renderForm('/posts/4/edit')
    expect(await screen.findByDisplayValue('기존')).toBeInTheDocument()
    expect(screen.getByLabelText(/태그/)).toHaveValue('x, y')
    await userEvent.clear(screen.getByLabelText('제목'))
    await userEvent.type(screen.getByLabelText('제목'), '바뀜')
    await userEvent.click(screen.getByRole('button', { name: '저장' }))
    await waitFor(() => expect(calls.at(-1).method).toBe('PUT'))
    expect(calls.at(-1)).toMatchObject({ url: '/api/v1/posts/4', body: { title: '바뀜', tags: ['x', 'y'], pinned: true } })
  })
})
