import { authHeaders } from '../auth/session.js'

// 서버와 주고받는 코드를 한곳에 모은다. 화면 컴포넌트는 fetch 를 직접 부르지 않는다.
const BASE = import.meta.env.VITE_API_BASE ?? ''

/** 서버가 2xx 가 아닌 상태로 응답했을 때 던진다. body 는 서버의 오류 본문(ErrorBody)이다. */
export class ApiError extends Error {
  constructor(status, body) {
    super(body?.message ?? `요청이 실패했습니다 (${status})`)
    this.status = status
    this.body = body
  }

  /** 서버의 issues([{ field, reason }])를 { field: reason } 으로 바꿔 입력 칸 옆에 보여 준다. */
  fieldErrors() {
    const result = {}
    for (const issue of this.body?.issues ?? []) {
      result[issue.field] = issue.reason
    }
    return result
  }
}

/** 값이 비어 있지 않은 항목만 질의 문자열로 만든다. */
export function toQuery(params) {
  const search = new URLSearchParams()
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null && String(value).trim() !== '') {
      search.set(key, String(value).trim())
    }
  }
  const text = search.toString()
  return text ? `?${text}` : ''
}

export async function request(path, { method = 'GET', body } = {}) {
  const headers = { ...authHeaders() }
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }
  const response = await fetch(`${BASE}${path}`, {
    method,
    headers: Object.keys(headers).length > 0 ? headers : undefined,
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  // 204 처럼 본문이 없는 응답도 있어서 본문이 비었는지 먼저 확인한다.
  const text = await response.text()
  const parsed = text ? JSON.parse(text) : null
  if (!response.ok) {
    // fetch 는 404, 400 같은 HTTP 오류에서도 예외를 던지지 않는다. 직접 확인해서 던져야 한다.
    throw new ApiError(response.status, parsed)
  }
  return parsed
}
