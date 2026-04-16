export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface TraceCode {
  id: number
  traceId: string
  batchId: number
  qrContent: string
  signValue: string
  codeStatus: number
  generatedAt: string
}

export interface BaseInfo {
  id: number
  baseCode: string
  baseName: string
  managerName: string
  contactPhone: string
  province: string
  city: string
  district: string
  address: string
  acreage: number
  status: number
  createdAt: string
  updatedAt: string
}

export interface ProductBatchInfo {
  id: number
  batchCode: string
  baseId: number
  baseName: string
  productName: string
  productCategory: string
  plantingDate: string
  expectedHarvestDate: string
  actualHarvestDate: string
  quantity: number
  unit: string
  batchStatus: number
  recallStatus: number
  remark: string
  createdAt: string
  updatedAt: string
}

export interface ProductionRecord {
  id: number
  batchId: number
  recordType: string
  operationTime: string
  operatorName: string
  materialName: string
  dosage: string
  content: string
  attachmentUrl: string
  createdAt: string
}

export interface InspectionReport {
  id: number
  batchId: number
  reportNo: string
  agencyName: string
  inspectorName: string
  inspectionTime: string
  resultStatus: number
  conclusion: string
  reportUrl: string
  createdAt: string
}

export interface RecallRecord {
  id: number
  batchId: number
  recallLevel: number
  reason: string
  recallStatus: number
  noticeTime: string
  closedAt: string
  createdAt: string
}

export interface TraceDetail {
  traceCode: TraceCode
  baseInfo: BaseInfo
  batchInfo: ProductBatchInfo
  productionRecords: ProductionRecord[]
  inspectionReports: InspectionReport[]
  recallRecord: RecallRecord | null
  recallWarning: boolean
}

export interface TraceHistoryItem {
  traceId: string
  productName: string
  queryTime: number
}

export interface FeedbackItem {
  id: string
  type: string
  content: string
  contact: string
  createdAt: number
  status: 'pending' | 'submitted' | 'failed'
}
