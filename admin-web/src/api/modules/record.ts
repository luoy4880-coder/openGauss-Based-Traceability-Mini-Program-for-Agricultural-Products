import http from '../http'

export function getProductionRecordPage(params: {
  batchId?: number | null
  recordType?: string
  pageNum: number
  pageSize: number
}) {
  return http.get('/api/production-records/page', { params })
}

export function createProductionRecord(data: {
  batchId: number | null
  recordType: string
  operationTime: string
  operatorName?: string
  materialName?: string
  dosage?: string
  content: string
  attachmentUrl?: string
}) {
  return http.post('/api/production-records', data)
}

export function updateProductionRecord(
  id: number,
  data: {
    batchId: number | null
    recordType: string
    operationTime: string
    operatorName?: string
    materialName?: string
    dosage?: string
    content: string
    attachmentUrl?: string
  },
) {
  return http.put(`/api/production-records/${id}`, data)
}

export function deleteProductionRecord(id: number) {
  return http.delete(`/api/production-records/${id}`)
}
