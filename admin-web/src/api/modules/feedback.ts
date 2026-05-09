import http from '../http'

export function getFeedbackTaskPage(params: {
  keyword?: string
  category?: string
  priority?: number | null
  riskLevel?: string
  status?: number | null
  assigneeUserId?: number | null
  pageNum: number
  pageSize: number
}) {
  return http.get('/api/feedback/page', { params })
}

export function getFeedbackOverview() {
  return http.get('/api/feedback/overview')
}

export function getFeedbackAssignees() {
  return http.get('/api/feedback/assignees')
}

export function handleFeedbackTask(
  id: number,
  data: {
    status: number
    assigneeUserId?: number | null
    handleNote?: string
    recall?: {
      enabled?: boolean
      batchId?: number | null
      recallLevel?: number
      reason?: string
    }
  },
) {
  return http.put(`/api/feedback/${id}/handle`, data)
}
