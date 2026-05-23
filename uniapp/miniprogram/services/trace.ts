import { API_BASE_URL, STORAGE_KEYS } from '../config/env'
import type {
  ApiResponse,
  FavoriteTraceItem,
  FeedbackItem,
  TraceDetail,
  TraceHistoryItem,
  TraceSummary,
} from '../types/trace'

type RequestMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

export type UserAiAnswer = {
  mode: 'USER'
  contextTitle: string
  answer: string
  references: Array<{ label: string; type: string; batchId?: number }>
}

function getCurrentUserId() {
  const userInfo = wx.getStorageSync('userInfo') as Record<string, unknown> | null
  const raw = userInfo?.id ?? userInfo?.userId
  if (raw === undefined || raw === null) return null
  const value = String(raw).trim()
  return value ? value : null
}

function scopedStorageKey(baseKey: string) {
  const userId = getCurrentUserId()
  return userId ? `${baseKey}_${userId}` : baseKey
}

function buildErrorMessage(url: string, errMsg: string) {
  if (errMsg.includes('timeout')) {
    return `请求超时：${API_BASE_URL}${url}`
  }
  return `网络异常：${API_BASE_URL}${url}`
}

function request<T>(url: string, options?: { method?: RequestMethod; data?: unknown; withAuth?: boolean }) {
  return new Promise<T>((resolve, reject) => {
    const token = wx.getStorageSync('token') as string
    const withAuth = options?.withAuth ?? false
    wx.request<ApiResponse<T>>({
      url: `${API_BASE_URL}${url}`,
      method: (options?.method || 'GET') as RequestMethod,
      data: options?.data,
      header: withAuth && token ? { Authorization: `Bearer ${token}` } : undefined,
      timeout: 10000,
      success: (response) => {
        const payload = response.data
        if (!payload) return reject(new Error('服务端未返回数据'))
        if (payload.code !== 200) return reject(new Error(payload.message || '请求失败'))
        resolve(payload.data)
      },
      fail: (error) => {
        const errMsg = (error as WechatMiniprogram.RequestFailCallbackResult)?.errMsg || ''
        reject(new Error(buildErrorMessage(url, errMsg)))
      },
    })
  })
}

function buildTraceQuery(traceId: string, signValue?: string) {
  const query = [`traceId=${encodeURIComponent(traceId)}`]
  if (signValue) {
    query.push(`sign=${encodeURIComponent(signValue)}`)
  }
  return query.join('&')
}

export function buildTraceDetailPageUrl(traceId: string, signValue?: string) {
  return `/pages/trace-detail/index?${buildTraceQuery(traceId, signValue)}`
}

export function fetchTraceDetail(traceId: string, signValue?: string) {
  const signQuery = signValue ? `?sign=${encodeURIComponent(signValue)}` : ''
  return request<TraceDetail>(`/api/trace/${encodeURIComponent(traceId)}${signQuery}`)
}

export function fetchTraceSummary(traceId: string, signValue?: string) {
  const signQuery = signValue ? `?sign=${encodeURIComponent(signValue)}` : ''
  return request<TraceSummary>(`/api/trace/${encodeURIComponent(traceId)}/summary${signQuery}`)
}

export function getTraceHistory() {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  const scoped = (wx.getStorageSync(key) as TraceHistoryItem[]) || []
  if (scoped.length > 0) return scoped
  if (key !== STORAGE_KEYS.traceHistory) return (wx.getStorageSync(STORAGE_KEYS.traceHistory) as TraceHistoryItem[]) || []
  return []
}

export function saveTraceHistory(item: TraceHistoryItem) {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  const history = getTraceHistory().filter((entry) => !(entry.traceId === item.traceId && (entry.signValue || '') === (item.signValue || '')))
  history.unshift(item)
  wx.setStorageSync(key, history.slice(0, 20))
}

export function removeTraceHistory(traceId: string, signValue?: string) {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  wx.setStorageSync(
    key,
    getTraceHistory().filter((entry) => !(entry.traceId === traceId && (entry.signValue || '') === (signValue || ''))),
  )
}

export function clearTraceHistory() {
  wx.removeStorageSync(scopedStorageKey(STORAGE_KEYS.traceHistory))
}

export function getFeedbackList() {
  return (wx.getStorageSync(scopedStorageKey(STORAGE_KEYS.feedbackList)) as FeedbackItem[]) || []
}

export function saveFeedbackLocal(item: Omit<FeedbackItem, 'id' | 'createdAt' | 'status'>, status: FeedbackItem['status']) {
  const key = scopedStorageKey(STORAGE_KEYS.feedbackList)
  const list = getFeedbackList()
  const payload: FeedbackItem = { ...item, id: `${Date.now()}`, createdAt: Date.now(), status }
  list.unshift(payload)
  wx.setStorageSync(key, list.slice(0, 20))
  return payload
}

export function submitFeedbackToServer(item: Omit<FeedbackItem, 'id' | 'createdAt' | 'status'> & { traceId?: string }) {
  return request<{ id: number }>(`/api/feedback`, { method: 'POST', data: item, withAuth: true })
}

export function fetchMyFeedback(limit = 20) {
  return request<FeedbackItem[]>(`/api/feedback/my?limit=${encodeURIComponent(String(limit))}`, { method: 'GET', withAuth: true })
}

export function askUserAi(question: string, traceId: string, sign?: string) {
  return request<UserAiAnswer>(`/api/ai/user-chat`, {
    method: 'POST',
    data: { question, traceId, sign },
  })
}

export function getFavoriteTraces() {
  return (wx.getStorageSync(scopedStorageKey(STORAGE_KEYS.favorites)) as FavoriteTraceItem[]) || []
}

export function isFavoriteTrace(traceId: string, signValue?: string) {
  return getFavoriteTraces().some((entry) => entry.traceId === traceId && (entry.signValue || '') === (signValue || ''))
}

export function saveFavoriteTrace(item: FavoriteTraceItem) {
  const key = scopedStorageKey(STORAGE_KEYS.favorites)
  const list = getFavoriteTraces().filter((entry) => !(entry.traceId === item.traceId && (entry.signValue || '') === (item.signValue || '')))
  list.unshift(item)
  wx.setStorageSync(key, list.slice(0, 50))
}

export function removeFavoriteTrace(traceId: string, signValue?: string) {
  const key = scopedStorageKey(STORAGE_KEYS.favorites)
  wx.setStorageSync(
    key,
    getFavoriteTraces().filter((entry) => !(entry.traceId === traceId && (entry.signValue || '') === (signValue || ''))),
  )
}

export function toggleFavoriteTrace(item: FavoriteTraceItem) {
  const exists = isFavoriteTrace(item.traceId, item.signValue)
  if (exists) {
    removeFavoriteTrace(item.traceId, item.signValue)
    return false
  }
  saveFavoriteTrace(item)
  return true
}
