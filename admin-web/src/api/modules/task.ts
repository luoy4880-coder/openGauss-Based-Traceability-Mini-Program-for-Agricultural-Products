import http from '../http'

export function getTaskPage(params: {
  status?: number | null
  assigneeUserId?: number | null
  keyword?: string
  pageNum: number
  pageSize: number
}) {
  return http.get('/api/tasks/page', { params })
}

export function claimTask(id: number) {
  return http.post(`/api/tasks/${id}/claim`)
}

export function completeTask(id: number) {
  return http.post(`/api/tasks/${id}/complete`)
}

export function reopenTask(id: number) {
  return http.post(`/api/tasks/${id}/reopen`)
}
