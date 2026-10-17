import { afterEach, describe, expect, it, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router'
import PostListPage from './PostListPage.jsx'
import { installFakeFetch } from '../test/fakeServer.js'

afterEach(() => vi.unstubAllGlobals())

const post = (id, extra = {}) => ({
  id, title: `글 ${id}`, authorName: '앨리스', tags: ['spring'], pinned: false, createdAt: '2026-10-17T09:30:00', ...extra,
})
const page = (items, total = items.length, number = 0) => ({ items, page: number, size: 10, total })

function renderAt(url) {
  return render(<MemoryRouter initialEntries={[url]}><PostListPage /></MemoryRouter>)
}

describe('PostListPage', () => {
  it('글 목록과 전체 건수를 보여 준다', async () => {
    installFakeFetch(() => ({ body: page([post(1), post(2, { pinned: true })]) }))
    renderAt('/posts')
    expect(await screen.findByText('글 1')).toBeInTheDocument()
    expect(screen.getByText('전체 2건')).toBeInTheDocument()
    expect(screen.getByText('[고정]')).toBeInTheDocument()
    expect(screen.getAllByText('2026-10-17 09:30')).toHaveLength(2)
  })

  it('결과가 없으면 안내 문구를 보여 준다', async () => {
    installFakeFetch(() => ({ body: page([]) }))
    renderAt('/posts')
    expect(await screen.findByText('조건에 맞는 글이 없습니다.')).toBeInTheDocument()
  })

  it('주소의 검색 조건을 서버에 보낸다', async () => {
    const { calls } = installFakeFetch(() => ({ body: page([]) }))
    renderAt('/posts?keyword=abc&tag=sql&page=1')
    await waitFor(() => expect(calls).toHaveLength(1))
    expect(calls[0].url).toBe('/api/v1/posts?keyword=abc&tag=sql&page=1&size=10')
  })

  it('검색하면 조건을 바꿔 다시 요청하고 첫 페이지로 돌아간다', async () => {
    const { calls } = installFakeFetch(() => ({ body: page([post(1)]) }))
    renderAt('/posts?page=3')
    await screen.findByText('글 1')
    await userEvent.type(screen.getByLabelText('검색어'), '스프링')
    await userEvent.click(screen.getByRole('button', { name: '검색' }))
    await waitFor(() => expect(calls.at(-1).url).toContain('keyword=%EC%8A%A4%ED%94%84%EB%A7%81'))
    expect(calls.at(-1).url).not.toContain('page=3')
  })

  it('다음 페이지 버튼이 다음 페이지를 요청한다', async () => {
    const { calls } = installFakeFetch((_m, url) => ({
      body: url.includes('page=1') ? page([post(11)], 25, 1) : page([post(1)], 25, 0),
    }))
    renderAt('/posts')
    await screen.findByText('글 1')
    expect(screen.getByRole('button', { name: '이전' })).toBeDisabled()
    await userEvent.click(screen.getByRole('button', { name: '다음' }))
    expect(await screen.findByText('글 11')).toBeInTheDocument()
    expect(calls.at(-1).url).toContain('page=1')
  })

  it('서버 오류를 화면에 보여 준다', async () => {
    installFakeFetch(() => ({ status: 400, body: { code: 'VALIDATION_FAILED', message: 'size가 올바르지 않습니다', issues: [] } }))
    renderAt('/posts')
    expect(await screen.findByRole('alert')).toHaveTextContent('size가 올바르지 않습니다')
  })
})
