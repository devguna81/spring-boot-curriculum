// 서버와 주고받는 코드를 한곳에 모은다. 화면 컴포넌트는 fetch 를 직접 부르지 않는다.
const BASE = import.meta.env.VITE_API_BASE ?? ''

/** 서버가 2xx 가 아닌 상태로 응답했을 때 던진다. body 는 서버의 오류 본문(ErrorBody)이다. */
export class ApiError extends Error {
  constructor(status, body) {
    super(body?.message ?? `요청이 실패했습니다 (${status})`)
    this.status = status
    this.body = body
  }
}

async function request(path, options = {}) {
  const response = await fetch(`${BASE}${path}`, {
    ...options,
    headers: options.body ? { 'Content-Type': 'application/json' } : undefined,
  })
  // 204 처럼 본문이 없는 응답도 있어서 본문이 비었는지 먼저 확인한다.
  const text = await response.text()
  const body = text ? JSON.parse(text) : null
  if (!response.ok) {
    // fetch 는 404, 400 같은 HTTP 오류에서도 예외를 던지지 않는다. 직접 확인해서 던져야 한다.
    throw new ApiError(response.status, body)
  }
  return body
}

export const usersApi = {
  list: () => request('/api/users'),
  get: (id) => request(`/api/users/${id}`),
  create: (user) => request('/api/users', { method: 'POST', body: JSON.stringify(user) }),
  update: (id, user) => request(`/api/users/${id}`, { method: 'PUT', body: JSON.stringify(user) }),
  remove: (id) => request(`/api/users/${id}`, { method: 'DELETE' }),
}
