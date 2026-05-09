import http from '../http'

export function loginApi(data: { username: string; password: string }) {
  return http.post('/api/auth/login', data)
}

export function registerApi(data: { username: string; password: string; realName?: string; phone?: string; companyName: string }) {
  return http.post('/api/auth/register', data)
}

export function getCurrentUser() {
  return http.get('/api/auth/me')
}

export function updateProfileApi(data: { realName: string; phone?: string }) {
  return http.put('/api/auth/profile', data)
}
