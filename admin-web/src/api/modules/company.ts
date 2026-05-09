import http from '../http'

export function getCompanyList() {
  return http.get('/api/companies')
}
