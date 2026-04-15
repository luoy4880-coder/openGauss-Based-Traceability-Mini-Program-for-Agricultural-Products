import http from '../http'

export function getRecallPage(params: {
  batchId?: number | null
  recallStatus?: number | null
  pageNum: number
  pageSize: number
}) {
  return http.get('/api/recalls/page', { params })
}

export function createRecall(data: {
  batchId: number | null
  recallLevel: number
  reason: string
}) {
  return http.post('/api/recalls', data)
}

export function closeRecall(id: number) {
  return http.put(`/api/recalls/${id}/close`)
}

export function deleteRecall(id: number) {
  return http.delete(`/api/recalls/${id}`)
}
