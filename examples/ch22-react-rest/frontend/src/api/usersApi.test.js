import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiError, usersApi } from './usersApi.js'
import { installFakeFetch } from '../test/fakeServer.js'

afterEach(() => vi.unstubAllGlobals())

describe('usersApi', () => {
  it('목록을 JSON 으로 읽는다', async () => {
    const { calls } = installFakeFetch(() => ({ body: [{ id: 1, name: 'kim' }] }))
    expect(await usersApi.list()).toEqual([{ id: 1, name: 'kim' }])
    expect(calls[0]).toMatchObject({ method: 'GET', url: '/api/users' })
    expect(calls[0].headers).toBeUndefined() // 본문이 없으면 Content-Type 을 붙이지 않는다
  })

  it('등록은 JSON 본문과 Content-Type 을 보낸다', async () => {
    const { calls } = installFakeFetch(() => ({ status: 201, body: { id: 5 } }))
    await usersApi.create({ name: 'new', email: 'n@example.com', age: 20 })
    expect(calls[0]).toMatchObject({
      method: 'POST',
      url: '/api/users',
      body: { name: 'new', email: 'n@example.com', age: 20 },
      headers: { 'Content-Type': 'application/json' },
    })
  })

  it('204 응답은 null 을 돌려준다', async () => {
    installFakeFetch(() => ({ status: 204 }))
    expect(await usersApi.remove(1)).toBeNull()
  })

  it('HTTP 오류는 ApiError 로 던지고 서버의 오류 본문을 담는다', async () => {
    installFakeFetch(() => ({
      status: 400,
      body: { code: 'VALIDATION_FAILED', message: '요청 값이 올바르지 않습니다', issues: [] },
    }))
    const error = await usersApi.create({}).catch((e) => e)
    expect(error).toBeInstanceOf(ApiError)
    expect(error.status).toBe(400)
    expect(error.message).toBe('요청 값이 올바르지 않습니다')
    expect(error.body.code).toBe('VALIDATION_FAILED')
  })

  it('본문 없는 오류 응답도 ApiError 가 된다', async () => {
    installFakeFetch(() => ({ status: 500 }))
    const error = await usersApi.list().catch((e) => e)
    expect(error).toBeInstanceOf(ApiError)
    expect(error.message).toBe('요청이 실패했습니다 (500)')
  })

  it('네트워크 오류는 fetch 가 던진 예외 그대로 전달된다', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')))
    await expect(usersApi.list()).rejects.toThrow('Failed to fetch')
  })
})
