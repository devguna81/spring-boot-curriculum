import { afterEach, describe, expect, it, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router'
import PostDetailPage from './PostDetailPage.jsx'
import { installFakeFetch } from '../test/fakeServer.js'

afterEach(() => vi.unstubAllGlobals())

const POST = { id: 3, title: '제목', content: '<b>굵게</b>\n본문', authorId: 10, authorName: '앨리스', tags: ['t'], pinned: false, createdAt: '2026-10-17T09:30:00' }

function renderDetail(me) {
  return render(
    <MemoryRouter initialEntries={['/posts/3']}>
      <Routes>
        <Route path="/posts/:id" element={<PostDetailPage me={me} />} />
        <Route path="/posts" element={<p>목록 화면</p>} />
      </Routes>
    </MemoryRouter>,
  )
}

describe('PostDetailPage', () => {
  it('본문의 HTML 은 해석하지 않고 글자로 보여 준다', async () => {
    installFakeFetch(() => ({ body: POST }))
    const { container } = renderDetail(null)
    expect(await screen.findByText('제목')).toBeInTheDocument()
    expect(container.querySelector('b')).toBeNull()
    expect(container.querySelector('.content')).toHaveTextContent('<b>굵게</b>')
  })

  it('작성자 본인에게는 수정/삭제를 보여 주고 다른 사람과 비로그인에게는 숨긴다', async () => {
    installFakeFetch(() => ({ body: POST }))
    const { unmount } = renderDetail({ id: 10, role: 'USER' })
    expect(await screen.findByRole('link', { name: '수정' })).toBeInTheDocument()
    unmount()

    const other = renderDetail({ id: 11, role: 'USER' })
    await screen.findByText('제목')
    expect(screen.queryByRole('link', { name: '수정' })).toBeNull()
    other.unmount()

    renderDetail(null)
    await screen.findByText('제목')
    expect(screen.queryByRole('button', { name: '삭제' })).toBeNull()
  })

  it('관리자는 남의 글도 수정할 수 있다', async () => {
    installFakeFetch(() => ({ body: POST }))
    renderDetail({ id: 1, role: 'ADMIN' })
    expect(await screen.findByRole('link', { name: '수정' })).toBeInTheDocument()
  })

  it('삭제를 확인하면 DELETE 후 목록으로 이동한다', async () => {
    vi.stubGlobal('confirm', vi.fn(() => true))
    const { calls } = installFakeFetch((method) => (method === 'DELETE' ? { status: 204 } : { body: POST }))
    renderDetail({ id: 10, role: 'USER' })
    await userEvent.click(await screen.findByRole('button', { name: '삭제' }))
    expect(await screen.findByText('목록 화면')).toBeInTheDocument()
    expect(calls.at(-1)).toMatchObject({ method: 'DELETE', url: '/api/v1/posts/3' })
  })

  it('삭제를 취소하면 요청하지 않는다', async () => {
    vi.stubGlobal('confirm', vi.fn(() => false))
    const { calls } = installFakeFetch(() => ({ body: POST }))
    renderDetail({ id: 10, role: 'USER' })
    await userEvent.click(await screen.findByRole('button', { name: '삭제' }))
    expect(calls).toHaveLength(1)
  })

  it('없는 글은 안내를 보여 준다', async () => {
    installFakeFetch(() => ({ status: 404, body: { code: 'NOT_FOUND', message: 'x', issues: [] } }))
    renderDetail(null)
    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('글을 찾을 수 없습니다.'))
  })
})
