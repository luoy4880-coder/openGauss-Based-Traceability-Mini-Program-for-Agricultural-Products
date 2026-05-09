import { API_BASE_URL, STORAGE_KEYS } from '../config/env'
import type { ApiResponse, FavoriteTraceItem, FeedbackItem, TraceDetail, TraceHistoryItem, TraceSummary } from '../types/trace'

type RequestMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

function getCurrentUserId() {
  const userInfo = wx.getStorageSync('userInfo') as any
  const raw = userInfo?.id ?? userInfo?.userId
  if (raw === undefined || raw === null) return null
  const value = String(raw).trim()
  return value ? value : null
}

function scopedStorageKey(baseKey: string) {
  const userId = getCurrentUserId()
  return userId ? `${baseKey}_${userId}` : baseKey
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
        if (!payload) return reject(new Error('Server returned no data'))
        if (payload.code !== 200) return reject(new Error(payload.message || 'Request failed'))
        resolve(payload.data)
      },
      fail: (error) => {
        const errMsg = (error as WechatMiniprogram.RequestFailCallbackResult)?.errMsg || ''
        if (errMsg.includes('timeout')) {
          return reject(new Error(`Request timeout: ${API_BASE_URL}${url}`))
        }
        reject(new Error(`Network error: ${API_BASE_URL}${url}`))
      },
    })
  })
}

export function fetchTraceDetail(traceId: string) { return request<TraceDetail>(`/api/trace/${encodeURIComponent(traceId)}`) }
export function fetchTraceSummary(traceId: string) { return request<TraceSummary>(`/api/trace/${encodeURIComponent(traceId)}/summary`) }

export function getTraceHistory() {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  const scoped = (wx.getStorageSync(key) as TraceHistoryItem[]) || []
  if (scoped.length > 0) return scoped
  if (key !== STORAGE_KEYS.traceHistory) return (wx.getStorageSync(STORAGE_KEYS.traceHistory) as TraceHistoryItem[]) || []
  return []
}

export function saveTraceHistory(item: TraceHistoryItem) {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  const history = getTraceHistory().filter((entry) => entry.traceId !== item.traceId)
  history.unshift(item)
  wx.setStorageSync(key, history.slice(0, 20))
}

export function clearTraceHistory() { wx.removeStorageSync(scopedStorageKey(STORAGE_KEYS.traceHistory)) }
export function getFeedbackList() { return (wx.getStorageSync(scopedStorageKey(STORAGE_KEYS.feedbackList)) as FeedbackItem[]) || [] }

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
  return request<any[]>(`/api/feedback/my?limit=${encodeURIComponent(String(limit))}`, { method: 'GET', withAuth: true })
}

export function removeTraceHistory(traceId: string) {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  wx.setStorageSync(key, getTraceHistory().filter((entry) => entry.traceId !== traceId))
}

export function getFavoriteTraces() { return (wx.getStorageSync(scopedStorageKey(STORAGE_KEYS.favorites)) as FavoriteTraceItem[]) || [] }

export function saveFavoriteTrace(item: FavoriteTraceItem) {
  const key = scopedStorageKey(STORAGE_KEYS.favorites)
  const list = getFavoriteTraces().filter((entry) => entry.traceId !== item.traceId)
  list.unshift(item)
  wx.setStorageSync(key, list.slice(0, 50))
}

export function removeFavoriteTrace(traceId: string) {
  const key = scopedStorageKey(STORAGE_KEYS.favorites)
  wx.setStorageSync(key, getFavoriteTraces().filter((entry) => entry.traceId !== traceId))
}
