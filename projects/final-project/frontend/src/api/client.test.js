import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { ApiError, toQuery } from './client.js'
import { postsApi } from './postsApi.js'
import { installFakeFetch } from '../test/fakeServer.js'
import { encodeBasic, login, logout } from '../auth/session.js'

beforeEach(() => logout())
afterEach(() => vi.unstubAllGlobals())

describe('toQuery', () => {
  it('빈 값은 빼고 나머지만 질의 문자열로 만든다', () => {
    expect(toQuery({ keyword: ' spring ', tag: '', page: 0, size: undefined })).toBe('?keyword=spring&page=0')
    expect(toQuery({ keyword: '' })).toBe('')
  })

  it('한글과 특수문자를 인코딩한다', () => {
    expect(toQuery({ keyword: 'a&b 가' })).toBe('?keyword=a%26b+%EA%B0%80')
  })
})

describe('postsApi', () => {
  it('검색 조건을 질의 문자열로 보내고 인증 헤더는 로그인했을 때만 붙인다', async () => {
    const { calls } = installFakeFetch(() => ({ body: { items: [], page: 0, size: 10, total: 0 } }))
    await postsApi.search({ keyword: '글', page: 2 })
    expect(calls[0].url).toBe('/api/v1/posts?keyword=%EA%B8%80&page=2&size=10')
    expect(calls[0].headers).toBeUndefined()

    login('alice', 'secret-1234')
    await postsApi.search()
    expect(calls[1].headers).toEqual({ Authorization: encodeBasic('alice', 'secret-1234') })
  })

  it('작성은 JSON 본문과 Content-Type 을 보낸다', async () => {
    login('bob', 'pw')
    const { calls } = installFakeFetch(() => ({ status: 201, body: { id: 9 } }))
    await postsApi.create({ title: 't', content: 'c', tags: ['a'], pinned: false })
    expect(calls[0]).toMatchObject({
      method: 'POST',
      url: '/api/v1/posts',
      body: { title: 't', content: 'c', tags: ['a'], pinned: false },
      headers: { 'Content-Type': 'application/json', Authorization: encodeBasic('bob', 'pw') },
    })
  })

  it('204 응답은 null 을 돌려준다', async () => {
    installFakeFetch(() => ({ status: 204 }))
    expect(await postsApi.remove(1)).toBeNull()
  })

  it('오류 응답은 ApiError 로 던지고 필드별 오류를 꺼낼 수 있다', async () => {
    installFakeFetch(() => ({
      status: 400,
      body: { code: 'VALIDATION_FAILED', message: '입력값이 올바르지 않습니다', issues: [{ field: 'title', reason: '필수' }] },
    }))
    const error = await postsApi.create({}).catch((e) => e)
    expect(error).toBeInstanceOf(ApiError)
    expect(error.status).toBe(400)
    expect(error.message).toBe('입력값이 올바르지 않습니다')
    expect(error.fieldErrors()).toEqual({ title: '필수' })
  })
})
