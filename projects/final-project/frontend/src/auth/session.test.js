import { beforeEach, describe, expect, it } from 'vitest'
import { authHeaders, getUserId, login, logout } from './session.js'

beforeEach(() => logout())

describe('session', () => {
  it('로그인하지 않으면 사용자도 헤더도 없다', () => {
    expect(getUserId()).toBeNull()
    expect(authHeaders()).toEqual({})
  })

  it('로그인하면 ID를 보관하고 헤더를 만든다', () => {
    login(12)
    expect(getUserId()).toBe('12')
    expect(authHeaders()).toEqual({ 'X-User-Id': '12' })
  })

  it('로그아웃하면 지워진다', () => {
    login(12)
    logout()
    expect(getUserId()).toBeNull()
  })
})
