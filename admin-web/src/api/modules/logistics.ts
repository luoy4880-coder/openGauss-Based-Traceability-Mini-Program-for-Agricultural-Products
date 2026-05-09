import http from '../http'

export interface LogisticsRecord {
  id: number
  batchId: number
  itemId?: number
  logisticsCode: string
  nodeType: string
  nodeName: string
  operationTime: string
  operatorName?: string
  contactPhone?: string
  location?: string
  temperature?: string
  humidity?: string
  attachmentUrl?: string
  remark?: string
  createdAt: string
}

export function getLogisticsRecords(params: { batchId?: number; itemId?: number }) {
  return http.get<LogisticsRecord[]>('/api/logistics-records', { params })
}

export function createLogisticsRecord(data: Partial<LogisticsRecord> & { batchId: number; nodeType: string; nodeName: string; operationTime: string }) {
  return http.post<LogisticsRecord>('/api/logistics-records', data)
}

export function updateLogisticsRecord(id: number, data: Partial<LogisticsRecord> & { batchId: number; nodeType: string; nodeName: string; operationTime: string }) {
  return http.put<LogisticsRecord>(`/api/logistics-records/${id}`, data)
}

export function deleteLogisticsRecord(id: number) {
  return http.delete(`/api/logistics-records/${id}`)
}
