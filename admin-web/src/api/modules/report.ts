import http from '../http'

export function getInspectionReportPage(params: {
  batchId?: number | null
  resultStatus?: number | null
  pageNum: number
  pageSize: number
}) {
  return http.get('/api/inspection-reports/page', { params })
}

export function createInspectionReport(data: {
  batchId: number | null
  reportNo: string
  agencyName: string
  inspectorName?: string
  inspectionTime: string
  resultStatus: number
  conclusion?: string
  reportUrl?: string
}) {
  return http.post('/api/inspection-reports', data)
}

export function updateInspectionReport(
  id: number,
  data: {
    batchId: number | null
    agencyName: string
    inspectorName?: string
    inspectionTime: string
    resultStatus: number
    conclusion?: string
    reportUrl?: string
  },
) {
  return http.put(`/api/inspection-reports/${id}`, data)
}

export function deleteInspectionReport(id: number) {
  return http.delete(`/api/inspection-reports/${id}`)
}
