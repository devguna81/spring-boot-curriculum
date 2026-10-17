import { request } from './client.js'

export const usersApi = {
  signup: (user) => request('/api/v1/users', { method: 'POST', body: user }),
  me: () => request('/api/v1/users/me'),
}
