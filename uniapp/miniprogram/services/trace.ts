import { API_BASE_URL, STORAGE_KEYS } from '../config/env'
import type {
  ApiResponse,
  FeedbackItem,
  TraceDetail,
  TraceHistoryItem,
} from '../types/trace'

type RequestMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

function getCurrentUserId() {
  const userInfo = wx.getStorageSync('userInfo') as any
  const raw = userInfo?.id ?? userInfo?.userId
  if (raw === undefined || raw === null) {
    return null
  }
  const value = String(raw).trim()
  return value ? value : null
}

function scopedStorageKey(baseKey: string) {
  const userId = getCurrentUserId()
  return userId ? `${baseKey}_${userId}` : baseKey
}

function request<T>(
  url: string,
  options?: {
    method?: RequestMethod
    data?: unknown
    withAuth?: boolean
  },
) {
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

        if (!payload) {
          reject(new Error('\u670d\u52a1\u7aef\u672a\u8fd4\u56de\u6570\u636e'))
          return
        }

        if (payload.code !== 200) {
          reject(new Error(payload.message || '\u8bf7\u6c42\u5931\u8d25'))
          return
        }

        resolve(payload.data)
      },
      fail: () => {
        reject(new Error('\u7f51\u7edc\u5f02\u5e38\uff0c\u8bf7\u68c0\u67e5\u540e\u7aef\u670d\u52a1\u548c\u8c03\u8bd5\u5730\u5740'))
      },
    })
  })
}

export function fetchTraceDetail(traceId: string) {
  return request<TraceDetail>(`/api/trace/${encodeURIComponent(traceId)}`)
}

export function getTraceHistory() {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  return (wx.getStorageSync(key) as TraceHistoryItem[]) || []
}

export function saveTraceHistory(item: TraceHistoryItem) {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  const history = getTraceHistory().filter((entry) => entry.traceId !== item.traceId)
  history.unshift(item)
  wx.setStorageSync(key, history.slice(0, 8))
}

export function clearTraceHistory() {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  wx.removeStorageSync(key)
}

export function getFeedbackList() {
  const key = scopedStorageKey(STORAGE_KEYS.feedbackList)
  return (wx.getStorageSync(key) as FeedbackItem[]) || []
}

export function saveFeedbackLocal(item: Omit<FeedbackItem, 'id' | 'createdAt' | 'status'>, status: FeedbackItem['status']) {
  const key = scopedStorageKey(STORAGE_KEYS.feedbackList)
  const list = getFeedbackList()
  const payload: FeedbackItem = {
    ...item,
    id: `${Date.now()}`,
    createdAt: Date.now(),
    status,
  }
  list.unshift(payload)
  wx.setStorageSync(key, list.slice(0, 20))
  return payload
}

export function submitFeedbackToServer(item: Omit<FeedbackItem, 'id' | 'createdAt' | 'status'> & { traceId?: string }) {
  return request<{ id: number }>(`/api/feedback`, {
    method: 'POST',
    data: item,
    withAuth: true,
  })
}

export function fetchMyFeedback(limit = 20) {
  return request<any[]>(`/api/feedback/my?limit=${encodeURIComponent(String(limit))}`, {
    method: 'GET',
    withAuth: true,
  })
}

export function removeTraceHistory(traceId: string) {
  const key = scopedStorageKey(STORAGE_KEYS.traceHistory)
  const history = getTraceHistory().filter((entry) => entry.traceId !== traceId)
  wx.setStorageSync(key, history)
}
