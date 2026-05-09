import http from '../http'

export interface TraceCode {
  id: number
  traceId: string
  batchId: number
  qrContent: string
  signValue: string
  codeStatus: number
  generatedAt: string
}

/**
 * 获取批次关联的溯源码列表
 */
export function getTraceCodes(batchId: number) {
  return http.get<TraceCode[]>('/api/trace-codes', { params: { batchId } })
}

/**
 * 为批次生成新的溯源码
 */
export function generateTraceCode(batchId: number) {
  // 注意：后端接口接收的是 TraceCodeGenerateRequest，其中包含 batchId 字段
  return http.post<TraceCode>('/api/trace-codes/generate', { batchId })
}