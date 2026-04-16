import { API_BASE_URL, API_ENDPOINTS } from '../../config/env'
import { clearTraceHistory, fetchMyFeedback, getFeedbackList, getTraceHistory } from '../../services/trace'
import type { FeedbackItem, TraceHistoryItem } from '../../types/trace'

Component({
  data: {
    apiBaseUrl: API_BASE_URL,
    lanHint: API_ENDPOINTS.lan,
    history: [] as TraceHistoryItem[],
    feedbackList: [] as FeedbackItem[],
    myFeedbackCount: 0,
    showDevInfo: false, // 默认隐藏开发信息
    isLoggedIn: false,
    userInfo: null as any,
    displayName: '游客用户',
    displayDesc: '暂未登录，数据仅保存在本地',
    loginType: (wx.getStorageSync('login_type') as string) || '',
  },
  lifetimes: {
    attached() {
      this.checkLoginStatus()
      this.refreshData()
    },
  },
  pageLifetimes: {
    show() {
      this.checkLoginStatus()
      this.refreshData()
    },
  },
  methods: {
    checkLoginStatus() {
      const token = wx.getStorageSync('token')
      const userInfo = wx.getStorageSync('userInfo')
      const loginType = wx.getStorageSync('login_type')
      const isLoggedIn = !!token
      const safeUserInfo = userInfo || null

      this.setData({
        isLoggedIn,
        userInfo: safeUserInfo,
        loginType: loginType || '',
        displayName: isLoggedIn
          ? safeUserInfo?.realName || safeUserInfo?.username || '已登录用户'
          : '游客用户',
        displayDesc: isLoggedIn ? '已登录，可提交反馈与查看本地记录' : '暂未登录，数据仅保存在本地',
      })
    },

    handleLogin() {
      wx.login({
        success: (res) => {
          if (res.code) {
            // 发送到后端进行登录
            wx.showLoading({ title: '登录中...' })
            wx.request({
              url: `${this.data.apiBaseUrl}/api/auth/wechat-login`,
              method: 'POST',
              data: { code: res.code },
              success: (loginRes: any) => {
                const payload = loginRes?.data
                if (payload && typeof payload === 'object' && payload.code === 200) {
                  const { token, ...userInfo } = payload.data
                  wx.setStorageSync('token', token)
                  wx.setStorageSync('userInfo', userInfo)
                  wx.setStorageSync('login_type', 'wechat')
                  this.fetchAndSyncUserProfile(token)
                } else {
                  wx.hideLoading()
                  const message =
                    payload && typeof payload === 'object'
                      ? payload.message || '登录失败'
                      : '登录失败（服务异常）'
                  wx.showToast({ title: message, icon: 'none', duration: 2500 })
                }
              },
              fail: () => {
                wx.hideLoading()
                wx.showToast({ title: '网络错误', icon: 'none' })
              },
            })
          }
        }
      })
    },

    fetchAndSyncUserProfile(token: string) {
      wx.request({
        url: `${this.data.apiBaseUrl}/api/auth/me`,
        method: 'GET',
        header: { Authorization: `Bearer ${token}` },
        success: (meRes: any) => {
          if (meRes.data && meRes.data.code === 200) {
            wx.setStorageSync('userInfo', meRes.data.data)
          }
          this.checkLoginStatus()
          this.refreshData()
          wx.showToast({ title: '登录成功', icon: 'success' })
        },
        fail: () => {
          this.checkLoginStatus()
          this.refreshData()
          wx.showToast({ title: '登录成功', icon: 'success' })
        },
        complete: () => {
          wx.hideLoading()
        },
      })
    },

    handleLogout() {
      wx.showModal({
        title: '退出登录',
        content: '确认退出当前账号吗？',
        success: ({ confirm }) => {
          if (!confirm) {
            return
          }
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.removeStorageSync('login_type')
          this.checkLoginStatus()
          this.setData({ myFeedbackCount: 0 })
          wx.showToast({ title: '已退出', icon: 'success' })
        },
      })
    },
    
    async refreshData() {
      this.setData({
        history: getTraceHistory(),
        feedbackList: getFeedbackList(),
      })

      const token = wx.getStorageSync('token')
      if (!token) {
        this.setData({ myFeedbackCount: 0 })
        return
      }

      try {
        const list = await fetchMyFeedback(20)
        this.setData({ myFeedbackCount: Array.isArray(list) ? list.length : 0 })
      } catch {
        this.setData({ myFeedbackCount: 0 })
      }
    },
    
    toggleDevInfo() {
      this.setData({
        showDevInfo: !this.data.showDevInfo
      })
    },

    goToHistory() {
      wx.navigateTo({
        url: '/pages/history/index',
      })
    },

    goToAccountLogin() {
      wx.navigateTo({
        url: '/pages/account/index?mode=login',
      })
    },

    goToRegister() {
      wx.navigateTo({
        url: '/pages/account/index?mode=register',
      })
    },

    goToBindAccount() {
      wx.navigateTo({
        url: '/pages/account/index?mode=bind',
      })
    },

    openTraceDetail(e: WechatMiniprogram.TouchEvent) {
      const { traceId } = e.currentTarget.dataset as { traceId: string }
      wx.navigateTo({
        url: `/pages/trace-detail/index?traceId=${traceId}`,
      })
    },

    clearHistory() {
      if (this.data.history.length === 0) {
        wx.showToast({
          title: '暂无可清空记录',
          icon: 'none',
        })
        return
      }

      wx.showModal({
        title: '清空记录',
        content: '确认删除本地查询历史吗？',
        success: ({ confirm }) => {
          if (!confirm) {
            return
          }

          clearTraceHistory()
          this.refreshData()
          wx.showToast({
            title: '已清空',
            icon: 'success',
          })
        },
      })
    },

    goToFeedbackPage() {
      wx.navigateTo({
        url: '/pages/feedback/index',
      })
    },
  },
})
