// 로그인 상태를 한곳에서 다룬다. 화면과 API 코드는 저장 방식을 모르고 이 모듈만 부른다.
//
// 서버가 HTTP Basic이라 매 요청에 "아이디:비밀번호"를 Base64로 바꾼 Authorization 헤더가 필요하다.
// 비밀번호 자체가 아니라 만들어 둔 헤더 값을 sessionStorage에 둔다. 탭을 닫으면 사라지지만,
// 같은 출처에서 스크립트가 실행되면(XSS) 읽힐 수 있다. 그래서 이 앱은 사용자 입력을 HTML로 해석하지 않는다.
const KEY = 'final-project.authorization'

/** 한글 비밀번호도 안전하게 Base64로 바꾼다. btoa는 Latin-1만 받는다. */
export function encodeBasic(username, password) {
  const bytes = new TextEncoder().encode(`${username}:${password}`)
  let binary = ''
  for (const byte of bytes) {
    binary += String.fromCharCode(byte)
  }
  return `Basic ${btoa(binary)}`
}

function read() {
  try {
    return sessionStorage.getItem(KEY)
  } catch {
    return null // 저장소를 쓸 수 없는 환경이면 로그인하지 않은 것으로 본다.
  }
}

export function isLoggedIn() {
  return read() !== null
}

export function login(username, password) {
  try {
    sessionStorage.setItem(KEY, encodeBasic(username, password))
  } catch {
    // 저장 실패는 무시한다. 새로고침하면 다시 로그인해야 할 뿐이다.
  }
}

export function logout() {
  try {
    sessionStorage.removeItem(KEY)
  } catch {
    // 위와 같다.
  }
}

/** 요청에 붙일 인증 헤더. 로그인하지 않았으면 빈 객체. */
export function authHeaders() {
  const value = read()
  return value ? { Authorization: value } : {}
}
