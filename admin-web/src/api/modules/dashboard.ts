import http from '../http'

export function getDashboardStats() {
  return http.get('/api/dashboard/stats')
}
