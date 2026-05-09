import http from '../http'

export function getUserPage(params: {
  keyword?: string
  status?: number | null
  pageNum: number
  pageSize: number
}) {
  return http.get('/api/users/page', { params })
}

export function getRoleList() {
  return http.get('/api/roles')
}

export function createUser(data: {
  username: string
  password: string
  realName: string
  phone?: string
  companyId: number
  status: number
  roleIds: number[]
}) {
  return http.post('/api/users', data)
}

export function updateUser(
  id: number,
  data: {
    realName: string
    phone?: string
    companyId: number
    status: number
    roleIds: number[]
  },
) {
  return http.put(`/api/users/${id}`, data)
}

export function updateUserPassword(id: number, data: { newPassword: string }) {
  return http.put(`/api/users/${id}/password`, data)
}

export function deleteUser(id: number) {
  return http.delete(`/api/users/${id}`)
}
