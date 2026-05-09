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

export interface ProductItem {
  id: number
  batchId: number
  itemCode: string
  traceId: string
  qrContent: string
  signValue: string
  itemStatus: number
  scanCount: number
  firstScannedAt: string
  lastScannedAt: string
  generatedAt: string
}

export interface TraceVerifyInfo {
  valid: boolean
  firstScan: boolean
  abnormal: boolean
  scanCount: number
  verifyMessage: string
  riskMessage: string
}

export interface LogisticsRecord {
  id: number
  batchId: number
  itemId?: number
  logisticsCode: string
  nodeType: string
  nodeName: string
  operationTime: string
  operatorName: string
  contactPhone: string
  location: string
  temperature: string
  humidity: string
  attachmentUrl: string
  remark: string
  createdAt: string
}

export interface TraceDetail {
  traceCode: TraceCode
  productItem: ProductItem | null
  baseInfo: BaseInfo
  batchInfo: ProductBatchInfo
  productionRecords: ProductionRecord[]
  inspectionReports: InspectionReport[]
  recallRecord: RecallRecord | null
  recallWarning: boolean
  verifyInfo: TraceVerifyInfo | null
  logisticsRecords: LogisticsRecord[]
}

export interface TraceHistoryItem {
  traceId: string
  productName: string
  batchId: number
  batchCode: string
  queryTime: number
}

export interface FeedbackItem {
  id: string
  type: string
  content: string
  contact: string
  traceId?: string
  batchId?: number
  createdAt: number
  status: 'pending' | 'submitted' | 'failed'
}

export interface TraceSummary {
  summaryTitle: string
  summaryText: string
  safetyLevel: 'HIGH_RISK' | 'SAFE' | 'CHECK' | 'CAUTION'
  trustScore: number
  highlights: string[]
  riskTips: string[]
  qualityInterpretation: string
  actionTips: string[]
}

export interface FavoriteTraceItem {
  traceId: string
  productName: string
  batchCode: string
  savedAt: number
}
