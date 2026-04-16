function formatTime(date) {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  const formatNumber = (n) => {
    const s = n.toString()
    return s[1] ? s : `0${s}`
  }

  return (
    [year, month, day].map(formatNumber).join('/') +
    ' ' +
    [hour, minute, second].map(formatNumber).join(':')
  )
}

Component({
  data: {
    history: [],
  },
  lifetimes: {
    attached() {
      this.refresh()
    },
  },
  pageLifetimes: {
    show() {
      this.refresh()
    },
  },
  methods: {
    getStorageKey() {
      const userInfo = wx.getStorageSync('userInfo') || {}
      const userId = userInfo.id || userInfo.userId
      return userId ? `trace_history_${userId}` : 'trace_history'
    },
    refresh() {
      const key = this.getStorageKey()
      const history = wx.getStorageSync(key) || []
      this.setData({ history })
    },
    formatTimestamp(value) {
      if (!value) {
        return '暂无'
      }
      return formatTime(new Date(value))
    },
    openTraceDetail(e) {
      const { traceId } = e.currentTarget.dataset
      wx.navigateTo({
        url: `/pages/trace-detail/index?traceId=${traceId}`,
      })
    },
    removeHistory(e) {
      const { traceId } = e.currentTarget.dataset
      const key = this.getStorageKey()
      const history = (wx.getStorageSync(key) || []).filter((item) => item.traceId !== traceId)
      wx.setStorageSync(key, history)
      this.refresh()
      wx.showToast({ title: '已删除', icon: 'none' })
    },
    clearHistory() {
      wx.showModal({
        title: '清空记录',
        content: '确认删除本地查询历史吗？',
        success: ({ confirm }) => {
          if (!confirm) {
            return
          }
          wx.removeStorageSync(this.getStorageKey())
          this.refresh()
          wx.showToast({ title: '已清空', icon: 'success' })
        },
      })
    },
    goToQuery() {
      wx.switchTab({
        url: '/pages/index/index',
      })
    },
  },
})
