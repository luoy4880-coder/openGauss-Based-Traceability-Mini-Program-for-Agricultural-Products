import type { TraceDetail, TraceSummary } from '../../types/trace'
import { API_BASE_URL } from '../../config/env'
import { fetchTraceDetail, fetchTraceSummary, isFavoriteTrace, toggleFavoriteTrace } from '../../services/trace'
import { formatDate, formatDateTime, joinAddress, recallLevelText, resultStatusText } from '../../utils/format'

type DetailChartMetric = { label: string; value: number; percent: number }

type DisplayTraceSummary = TraceSummary & {
  safetyLabel: string
  safetyDesc: string
  scorePercent: number
  themeClass: string
}

type DisplayTraceDetail = TraceDetail & {
  baseInfo: TraceDetail['baseInfo'] & { fullAddress: string }
  batchInfo: TraceDetail['batchInfo'] & {
    plantingDateText: string
    expectedHarvestDateText: string
    actualHarvestDateText: string
  }
  productionRecords: Array<TraceDetail['productionRecords'][number] & { operationTimeText: string }>
  inspectionReports: Array<TraceDetail['inspectionReports'][number] & {
    inspectionTimeText: string
    resultStatusLabel: string
    reportName: string
  }>
  recallRecord: (NonNullable<TraceDetail['recallRecord']> & {
    recallLevelLabel: string
    noticeTimeText: string
  }) | null
  logisticsRecords: Array<TraceDetail['logisticsRecords'][number] & { operationTimeText: string }>
}

function toAbsoluteUrl(rawUrl: string) {
  const value = (rawUrl || '').trim()
  if (!value) return ''
  if (/^https?:\/\//i.test(value)) return value
  const base = API_BASE_URL.replace(/\/$/, '')
  return `${base}${value.startsWith('/') ? value : `/${value}`}`
}

function buildDetailChartMetrics(detail: TraceDetail): DetailChartMetric[] {
  const metrics = [
    { label: '生产记录', value: detail.productionRecords.length },
    { label: '质检报告', value: detail.inspectionReports.length },
    { label: '物流节点', value: detail.logisticsRecords?.length || 0 },
    { label: '召回预警', value: detail.recallWarning ? 1 : 0 },
  ]
  const max = Math.max(...metrics.map((item) => item.value), 1)
  return metrics.map((item) => ({
    ...item,
    percent: item.value === 0 ? 8 : Math.max(16, Math.round((item.value / max) * 100)),
  }))
}

function calcInfoIntegrity(detail: TraceDetail): number {
  const checks = [
    detail.baseInfo.baseName,
    detail.baseInfo.managerName,
    detail.batchInfo.batchCode,
    detail.batchInfo.productName,
    detail.batchInfo.productCategory,
    detail.batchInfo.plantingDate,
    detail.batchInfo.actualHarvestDate,
    detail.productionRecords.length > 0 ? 'yes' : '',
    detail.inspectionReports.length > 0 ? 'yes' : '',
    detail.logisticsRecords?.length > 0 ? 'yes' : '',
    detail.verifyInfo?.valid ? 'yes' : '',
  ]
  return Math.round((checks.filter(Boolean).length / checks.length) * 100)
}

function buildDisplaySummary(summary: TraceSummary): DisplayTraceSummary {
  const score = Math.max(0, Math.min(Number(summary.trustScore) || 0, 100))
  const themeMap: Record<TraceSummary['safetyLevel'], Pick<DisplayTraceSummary, 'safetyLabel' | 'safetyDesc' | 'themeClass'>> = {
    SAFE: {
      safetyLabel: '状态稳定',
      safetyDesc: '当前追溯信息完整，未发现明显异常信号。',
      themeClass: 'summary-safe',
    },
    CHECK: {
      safetyLabel: '建议核验',
      safetyDesc: '部分节点需要人工进一步确认后再判断。',
      themeClass: 'summary-check',
    },
    CAUTION: {
      safetyLabel: '谨慎查看',
      safetyDesc: '存在需要留意的信息，建议结合质检与物流记录判断。',
      themeClass: 'summary-caution',
    },
    HIGH_RISK: {
      safetyLabel: '风险提示',
      safetyDesc: '摘要识别到较强风险信号，建议优先核对详情记录。',
      themeClass: 'summary-risk',
    },
  }

  return {
    ...summary,
    ...themeMap[summary.safetyLevel],
    scorePercent: score,
  }
}

function buildDisplayDetail(detail: TraceDetail): DisplayTraceDetail {
  return {
    ...detail,
    baseInfo: {
      ...detail.baseInfo,
      fullAddress: joinAddress([
        detail.baseInfo.province,
        detail.baseInfo.city,
        detail.baseInfo.district,
        detail.baseInfo.address,
      ]),
    },
    batchInfo: {
      ...detail.batchInfo,
      plantingDateText: formatDate(detail.batchInfo.plantingDate),
      expectedHarvestDateText: formatDate(detail.batchInfo.expectedHarvestDate),
      actualHarvestDateText: formatDate(detail.batchInfo.actualHarvestDate),
    },
    productionRecords: detail.productionRecords.map((item) => ({
      ...item,
      operationTimeText: formatDateTime(item.operationTime),
    })),
    inspectionReports: detail.inspectionReports.map((item) => ({
      ...item,
      inspectionTimeText: formatDateTime(item.inspectionTime),
      resultStatusLabel: resultStatusText(item.resultStatus),
      reportName: item.reportNo || '检测报告',
    })),
    recallRecord: detail.recallRecord
      ? {
          ...detail.recallRecord,
          recallLevelLabel: recallLevelText(detail.recallRecord.recallLevel),
          noticeTimeText: formatDateTime(detail.recallRecord.noticeTime),
        }
      : null,
    logisticsRecords: (detail.logisticsRecords || []).map((item) => ({
      ...item,
      operationTimeText: formatDateTime(item.operationTime),
    })),
  }
}

Page({
  data: {
    loading: true,
    traceId: '',
    signValue: '',
    detail: null as DisplayTraceDetail | null,
    summary: null as DisplayTraceSummary | null,
    errorMessage: '',
    detailChart: [] as DetailChartMetric[],
    integrityRate: 0,
    isFavorite: false,
  },

  onLoad(options: { traceId?: string; sign?: string }) {
    const traceId = options.traceId || ''
    const signValue = options.sign || ''
    this.setData({ traceId, signValue })
    this.loadTraceDetail(traceId, signValue)
  },

  onShow() {
    this.syncFavoriteState()
  },

  syncFavoriteState() {
    const { traceId, signValue } = this.data
    if (!traceId) return
    this.setData({
      isFavorite: isFavoriteTrace(traceId, signValue),
    })
  },

  async loadTraceDetail(traceId: string, signValue?: string) {
    if (!traceId) {
      this.setData({ loading: false, errorMessage: '缺少溯源码参数' })
      return
    }
    this.setData({ loading: true, errorMessage: '' })
    try {
      const [detail, summary] = await Promise.all([
        fetchTraceDetail(traceId, signValue),
        fetchTraceSummary(traceId, signValue),
      ])
      this.setData({
        detail: buildDisplayDetail(detail),
        summary: buildDisplaySummary(summary),
        loading: false,
        detailChart: buildDetailChartMetrics(detail),
        integrityRate: calcInfoIntegrity(detail),
      })
      this.syncFavoriteState()
    } catch (error) {
      this.setData({
        loading: false,
        errorMessage: error instanceof Error ? error.message : '加载失败',
        detailChart: [],
        integrityRate: 0,
      })
    }
  },

  retryLoad() {
    this.loadTraceDetail(this.data.traceId, this.data.signValue)
  },

  previewReportFile(e: WechatMiniprogram.TouchEvent) {
    const { url, name } = e.currentTarget.dataset as { url?: string; name?: string }
    const fullUrl = toAbsoluteUrl(url || '')
    if (!fullUrl) {
      wx.showToast({ title: '报告地址无效', icon: 'none' })
      return
    }
    wx.navigateTo({
      url: `/pages/report-preview/index?url=${encodeURIComponent(fullUrl)}&name=${encodeURIComponent(name || '检测报告')}`,
    })
  },

  openQualityInterpretation() {
    const signQuery = this.data.signValue ? `&sign=${encodeURIComponent(this.data.signValue)}` : ''
    wx.navigateTo({
      url: `/pages/quality-interpretation/index?traceId=${encodeURIComponent(this.data.traceId)}${signQuery}`,
    })
  },

  openLogisticsTimeline() {
    const signQuery = this.data.signValue ? `&sign=${encodeURIComponent(this.data.signValue)}` : ''
    wx.navigateTo({
      url: `/pages/logistics-timeline/index?traceId=${encodeURIComponent(this.data.traceId)}${signQuery}`,
    })
  },

  openAiQa() {
    const signQuery = this.data.signValue ? `&sign=${encodeURIComponent(this.data.signValue)}` : ''
    wx.navigateTo({
      url: `/pages/ai-qa/index?traceId=${encodeURIComponent(this.data.traceId)}${signQuery}`,
    })
  },

  toggleCurrentFavorite() {
    if (!this.data.detail) return
    const saved = toggleFavoriteTrace({
      traceId: this.data.detail.traceCode.traceId,
      signValue: this.data.signValue || this.data.detail.traceCode.signValue || '',
      productName: this.data.detail.batchInfo.productName || '产品',
      batchCode: this.data.detail.batchInfo.batchCode || '',
      savedAt: Date.now(),
    })
    this.setData({ isFavorite: saved })
    wx.showToast({ title: saved ? '已加入收藏' : '已取消收藏', icon: 'success' })
  },
})
