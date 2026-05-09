import { fetchTraceDetail } from '../../services/trace'
import { formatDateTime } from '../../utils/format'

Page({
  data: {
    list: [] as any[],
    loading: true,
  },
  onLoad(options: { traceId?: string }) {
    this.loadData(options.traceId || '')
  },
  async loadData(traceId: string) {
    try {
      const detail = await fetchTraceDetail(traceId)
      this.setData({
        list: (detail.logisticsRecords || []).map((item) => ({
          ...item,
          operationTimeText: formatDateTime(item.operationTime),
        })),
        loading: false,
      })
    } catch {
      this.setData({ list: [], loading: false })
    }
  },
})
