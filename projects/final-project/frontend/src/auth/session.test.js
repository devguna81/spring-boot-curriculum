import { beforeEach, describe, expect, it } from 'vitest'
import { authHeaders, encodeBasic, isLoggedIn, login, logout } from './session.js'

beforeEach(() => logout())

describe('encodeBasic', () => {
  it('아이디:비밀번호를 Base64로 바꾼다', () => {
    expect(encodeBasic('alice', 'secret')).toBe('Basic YWxpY2U6c2VjcmV0')
  })

  it('한글 비밀번호도 UTF-8 기준으로 바꾼다', () => {
    // btoa에 한글을 그대로 넣으면 예외가 난다. 서버(Spring)는 UTF-8로 풀어 비교한다.
    expect(encodeBasic('kim', '비밀번호1234')).toBe(`Basic ${Buffer.from('kim:비밀번호1234', 'utf-8').toString('base64')}`)
  })
})

describe('session', () => {
  it('로그인하지 않으면 헤더가 없다', () => {
    expect(isLoggedIn()).toBe(false)
    expect(authHeaders()).toEqual({})
  })

  it('로그인하면 Authorization 헤더를 만든다', () => {
    login('alice', 'secret')
    expect(isLoggedIn()).toBe(true)
    expect(authHeaders()).toEqual({ Authorization: 'Basic YWxpY2U6c2VjcmV0' })
  })

  it('저장소에는 비밀번호 원문이 남지 않는다', () => {
    login('alice', 'plain-secret-123')
    expect(JSON.stringify({ ...sessionStorage })).not.toContain('plain-secret-123')
  })

  it('로그아웃하면 지워진다', () => {
    login('alice', 'secret')
    logout()
    expect(isLoggedIn()).toBe(false)
  })
})
