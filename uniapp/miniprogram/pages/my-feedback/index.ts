import { fetchMyFeedback } from '../../services/trace'

function statusText(status: number) {
  if (status === 2) return '已完成'
  if (status === 1) return '处理中'
  return '待处理'
}

function priorityText(priority?: number) {
  if (priority === 1) return '高'
  if (priority === 2) return '中'
  return '低'
}

Component({
  data: {
    list: [] as any[],
    loading: true,
  },
  lifetimes: {
    attached() {
      this.loadData()
    },
  },
  pageLifetimes: {
    show() {
      this.loadData()
    },
  },
  methods: {
    async loadData() {
      try {
        const list = await fetchMyFeedback(30)
        this.setData({
          list: (list || []).map((item: any) => ({
            ...item,
            statusText: statusText(item.status),
            priorityText: priorityText(item.aiPriority),
          })),
          loading: false,
        })
      } catch {
        this.setData({ list: [], loading: false })
      }
    },
  },
})
