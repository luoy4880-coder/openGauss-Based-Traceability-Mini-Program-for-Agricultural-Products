import http from '../http'

export function getBatchPage(params: {
  keyword?: string
  baseId?: number | null
  batchStatus?: number | null
  pageNum: number
  pageSize: number
}) {
  return http.get('/api/batches/page', { params })
}

export function createBatch(data: {
  batchCode: string
  baseId: number | null
  productName: string
  productCategory?: string
  plantingDate?: string
  expectedHarvestDate?: string
  actualHarvestDate?: string
  quantity?: number | null
  unit?: string
  batchStatus: number
  recallStatus: number
  remark?: string
}) {
  return http.post('/api/batches', data)
}

export function updateBatch(
  id: number,
  data: {
    baseId: number | null
    productName: string
    productCategory?: string
    plantingDate?: string
    expectedHarvestDate?: string
    actualHarvestDate?: string
    quantity?: number | null
    unit?: string
    batchStatus: number
    recallStatus: number
    remark?: string
  },
) {
  return http.put(`/api/batches/${id}`, data)
}

export function deleteBatch(id: number) {
  return http.delete(`/api/batches/${id}`)
}

export function getBatchList(params?: {
  keyword?: string
  baseId?: number | null
  batchStatus?: number | null
}) {
  return http.get('/api/batches', { params })
}
