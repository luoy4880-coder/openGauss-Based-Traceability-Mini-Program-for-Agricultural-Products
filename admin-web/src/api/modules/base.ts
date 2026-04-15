import http from '../http'

export function getBasePage(params: {
  keyword?: string
  status?: number | null
  pageNum: number
  pageSize: number
}) {
  return http.get('/api/bases/page', { params })
}

export function createBase(data: {
  baseCode: string
  baseName: string
  managerName?: string
  contactPhone?: string
  province?: string
  city?: string
  district?: string
  address?: string
  acreage?: number | null
  status: number
}) {
  return http.post('/api/bases', data)
}

export function updateBase(
  id: number,
  data: {
    baseName: string
    managerName?: string
    contactPhone?: string
    province?: string
    city?: string
    district?: string
    address?: string
    acreage?: number | null
    status: number
  },
) {
  return http.put(`/api/bases/${id}`, data)
}

export function deleteBase(id: number) {
  return http.delete(`/api/bases/${id}`)
}

export function getBaseList(params?: { keyword?: string; status?: number | null }) {
  return http.get('/api/bases', { params })
}
