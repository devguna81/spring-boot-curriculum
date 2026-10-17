import { request, toQuery } from './client.js'

export const postsApi = {
  search: ({ keyword, tag, page = 0, size = 10 } = {}) =>
    request(`/api/v1/posts${toQuery({ keyword, tag, page, size })}`),
  get: (id) => request(`/api/v1/posts/${id}`),
  create: (post) => request('/api/v1/posts', { method: 'POST', body: post }),
  update: (id, post) => request(`/api/v1/posts/${id}`, { method: 'PUT', body: post }),
  remove: (id) => request(`/api/v1/posts/${id}`, { method: 'DELETE' }),
}
