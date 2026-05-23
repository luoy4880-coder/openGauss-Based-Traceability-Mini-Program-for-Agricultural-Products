import { API_BASE_URL } from '../../config/env'
import { fetchMyFeedback, getFavoriteTraces, getFeedbackList, getTraceHistory } from '../../services/trace'
import type { FeedbackItem, TraceHistoryItem } from '../../types/trace'

Page({
  data: {
    apiBaseUrl: API_BASE_URL,
    history: [] as TraceHistoryItem[],
    feedbackList: [] as FeedbackItem[],
    myFeedbackCount: 0,
    favoriteCount: 0,
    isLoggedIn: false,
    userInfo: null as any,
    displayName: '游客用户',
    displayDesc: '未登录，数据仅保存在本地。',
    loginType: (wx.getStorageSync('login_type') as string) || '',
  },
  onLoad() {
    this.checkLoginStatus()
    this.refreshData()
  },
  onShow() {
    this.checkLoginStatus()
    this.refreshData()
  },
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
      displayName: isLoggedIn ? safeUserInfo?.realName || safeUserInfo?.username || '已登录用户' : '游客用户',
      displayDesc: isLoggedIn ? '已登录，可查看收藏、反馈与查询历史。' : '未登录，数据仅保存在本地。',
    })
  },
  handleLogin() {
    wx.login({
      success: (res) => {
        if (!res.code) return
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
              wx.showToast({ title: payload?.message || '登录失败', icon: 'none', duration: 2500 })
            }
          },
          fail: () => {
            wx.hideLoading()
            wx.showToast({ title: '网络错误', icon: 'none' })
          },
        })
      },
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
      complete: () => wx.hideLoading(),
    })
  },
  handleLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确认退出当前账号吗？',
      success: ({ confirm }) => {
        if (!confirm) return
        wx.removeStorageSync('token')
        wx.removeStorageSync('userInfo')
        wx.removeStorageSync('login_type')
        this.checkLoginStatus()
        this.refreshData()
        wx.showToast({ title: '已退出', icon: 'success' })
      },
    })
  },
  async refreshData() {
    this.setData({
      history: getTraceHistory(),
      feedbackList: getFeedbackList(),
      favoriteCount: getFavoriteTraces().length,
    })
    const token = wx.getStorageSync('token')
    if (!token) {
      return this.setData({ myFeedbackCount: 0 })
    }
    try {
      const list = await fetchMyFeedback(20)
      this.setData({ myFeedbackCount: Array.isArray(list) ? list.length : 0 })
    } catch {
      this.setData({ myFeedbackCount: 0 })
    }
  },
  goToHistory() { wx.navigateTo({ url: '/pages/history/index' }) },
  goToCollection() { wx.switchTab({ url: '/pages/collection/index' }) },
  goToMyFeedback() { wx.navigateTo({ url: '/pages/my-feedback/index' }) },
  goToAccountLogin() { wx.navigateTo({ url: '/pages/account/index?mode=login' }) },
  goToRegister() { wx.navigateTo({ url: '/pages/account/index?mode=register' }) },
  goToBindAccount() { wx.navigateTo({ url: '/pages/account/index?mode=bind' }) },
  goToFeedbackPage() { wx.switchTab({ url: '/pages/feedback/index' }) },
})
