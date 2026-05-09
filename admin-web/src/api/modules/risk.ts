import http from '../http'

export function getRiskOverview() {
  return http.get('/api/risk/overview')
}
