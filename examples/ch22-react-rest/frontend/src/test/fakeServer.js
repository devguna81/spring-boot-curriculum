import { vi } from 'vitest'

/** fetch 를 가짜로 바꾼다. 요청은 calls 에 기록되고, 응답은 handler(method, path, body) 가 정한다. */
export function installFakeFetch(handler) {
  const calls = []
  const fake = vi.fn(async (url, options = {}) => {
    const method = options.method ?? 'GET'
    const body = options.body ? JSON.parse(options.body) : undefined
    calls.push({ method, url, body, headers: options.headers })
    const result = await handler(method, url, body)
    const status = result.status ?? 200
    return new Response(result.body === undefined ? null : JSON.stringify(result.body), { status })
  })
  vi.stubGlobal('fetch', fake)
  return { calls, fake }
}
