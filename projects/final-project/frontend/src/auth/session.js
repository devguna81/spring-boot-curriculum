// 로그인 상태를 한곳에서 다룬다. 화면과 API 코드는 저장 방식을 모르고 이 모듈만 부른다.
// 지금은 서버의 임시 인증(X-User-Id)에 맞춰 사용자 ID만 보관한다. f04에서 아이디/비밀번호 방식으로 바꾼다.
const KEY = 'final-project.userId'

export function getUserId() {
  try {
    return sessionStorage.getItem(KEY)
  } catch {
    return null // 저장소를 쓸 수 없는 환경이면 로그인하지 않은 것으로 본다.
  }
}

export function login(userId) {
  try {
    sessionStorage.setItem(KEY, String(userId))
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
  const id = getUserId()
  return id ? { 'X-User-Id': id } : {}
}
