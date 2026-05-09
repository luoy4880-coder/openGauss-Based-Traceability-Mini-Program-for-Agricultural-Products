import http from '../http'

export interface ProductItem {
  id: number
  batchId: number
  itemCode: string
  traceId: string
  qrContent: string
  signValue: string
  itemStatus: number
  scanCount: number
  firstScannedAt?: string
  lastScannedAt?: string
  generatedAt: string
}

export function getProductItems(batchId: number) {
  return http.get<ProductItem[]>('/api/product-items', { params: { batchId } })
}

export function generateProductItems(batchId: number, quantity: number) {
  return http.post<ProductItem[]>('/api/product-items/generate', { batchId, quantity })
}
