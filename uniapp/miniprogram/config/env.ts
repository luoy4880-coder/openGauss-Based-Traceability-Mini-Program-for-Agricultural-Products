const LOCAL_API_BASE_URL = 'http://127.0.0.1:8080'
const LAN_API_BASE_URL = 'http://10.195.214.101:8080'

export const API_BASE_URL = LOCAL_API_BASE_URL

export const API_ENDPOINTS = {
  local: LOCAL_API_BASE_URL,
  lan: LAN_API_BASE_URL,
}

export const STORAGE_KEYS = {
  traceHistory: 'trace_history',
  feedbackList: 'feedback_list',
  favorites: 'favorite_traces',
}
