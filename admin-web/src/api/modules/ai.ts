import http from '../http'

export type AiChatReference = {
  type: 'summary' | 'batch_archive' | 'trace'
  label: string
  batchId?: number
}

export type AiChatAnswer = {
  mode: 'STAFF' | 'USER'
  contextTitle: string
  answer: string
  references: AiChatReference[]
}

export function askStaffAi(data: { question: string; batchId?: number | null }) {
  return http.post<AiChatAnswer>('/api/ai/staff-chat', data)
}
