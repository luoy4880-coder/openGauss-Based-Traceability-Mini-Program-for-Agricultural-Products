import { fetchTraceSummary } from '../../services/trace'
Component({
  data: { traceId: '', summary: null as any, loading: true },
  onLoad(options: { traceId?: string }) {
    const traceId = options.traceId || ''
    this.setData({ traceId })
    this.loadSummary(traceId)
  },
  async loadSummary(traceId: string) {
    try { this.setData({ summary: await fetchTraceSummary(traceId), loading: false }) } catch { this.setData({ loading: false }) }
  },
})
