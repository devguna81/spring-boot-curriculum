import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router'
import { describe, expect, it } from 'vitest'
import App from './App.jsx'

// 실제 브라우저 주소 대신 메모리 안의 주소로 라우터를 구동한다.
function renderAt(path) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <App />
    </MemoryRouter>,
  )
}

describe('목록 화면', () => {
  it('모든 사용자를 행으로 그린다', () => {
    renderAt('/users')
    expect(screen.getAllByRole('row')).toHaveLength(1 + 4) // 헤더 1 + 데이터 4
    expect(screen.getByTestId('count')).toHaveTextContent('4')
  })

  it('루트 경로도 목록을 보여 준다', () => {
    renderAt('/')
    expect(screen.getByRole('heading', { name: '사용자 목록' })).toBeInTheDocument()
  })

  it('비활성 사용자 행에 inactive 클래스를 붙인다', () => {
    renderAt('/users')
    const row = screen.getByText('park@example.com').closest('tr')
    expect(row).toHaveClass('inactive')
    expect(within(row).getByText('비활성')).toBeInTheDocument()
  })

  it('활성만 보기를 켜면 비활성 사용자가 사라진다', async () => {
    const user = userEvent.setup()
    renderAt('/users')
    await user.click(screen.getByLabelText('활성만 보기'))
    expect(screen.queryByText('park@example.com')).not.toBeInTheDocument()
    expect(screen.getByTestId('count')).toHaveTextContent('3')
  })

  it('이름 검색은 대소문자를 가리지 않는다', async () => {
    const user = userEvent.setup()
    renderAt('/users')
    await user.type(screen.getByLabelText('이름 검색'), 'KIM')
    expect(screen.getByTestId('count')).toHaveTextContent('1')
    expect(screen.getByText('kim@example.com')).toBeInTheDocument()
  })

  it('검색 결과가 없으면 안내 문구를 보여 준다', async () => {
    const user = userEvent.setup()
    renderAt('/users')
    await user.type(screen.getByLabelText('이름 검색'), 'zzz')
    expect(screen.getByText('표시할 사용자가 없습니다.')).toBeInTheDocument()
    expect(screen.queryByRole('table')).not.toBeInTheDocument()
  })

  it('문서 제목을 effect 로 바꾼다', () => {
    renderAt('/users')
    expect(document.title).toBe('사용자 목록 - ch21')
  })

  it('이름에 들어 있는 태그를 HTML 로 해석하지 않는다', () => {
    const { container } = renderAt('/users')
    expect(screen.getByText('<b>choi</b>')).toBeInTheDocument()
    expect(container.querySelector('b')).toBeNull()
  })
})

describe('상세 화면과 라우팅', () => {
  it('링크를 누르면 상세 화면으로 이동한다', async () => {
    const user = userEvent.setup()
    renderAt('/users')
    await user.click(screen.getByRole('link', { name: 'lee' }))
    expect(screen.getByRole('heading', { name: '사용자 상세' })).toBeInTheDocument()
    expect(screen.getByText('lee@example.com')).toBeInTheDocument()
    expect(document.title).toBe('lee - ch21')
  })

  it('목록으로 돌아올 수 있다', async () => {
    const user = userEvent.setup()
    renderAt('/users/1')
    await user.click(screen.getByRole('link', { name: '목록으로' }))
    expect(screen.getByRole('heading', { name: '사용자 목록' })).toBeInTheDocument()
  })

  it('없는 사용자는 찾을 수 없음 화면을 보여 준다', () => {
    renderAt('/users/999')
    expect(screen.getByRole('heading', { name: '찾을 수 없습니다' })).toBeInTheDocument()
    expect(document.title).toBe('찾을 수 없음 - ch21')
  })

  it('정의되지 않은 경로도 찾을 수 없음 화면이다', () => {
    renderAt('/nope')
    expect(screen.getByRole('heading', { name: '찾을 수 없습니다' })).toBeInTheDocument()
  })
})
