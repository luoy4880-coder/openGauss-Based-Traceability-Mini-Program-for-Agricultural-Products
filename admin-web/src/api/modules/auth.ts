import http from '../http'

export function loginApi(data: { username: string; password: string }) {
  return http.post('/api/auth/login', data)
}

export function getCurrentUser() {
  return http.get('/api/auth/me')
}
