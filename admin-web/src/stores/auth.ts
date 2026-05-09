import { defineStore } from 'pinia'
import { getCurrentUser, loginApi } from '../api/modules/auth'
import type { CurrentUser, LoginResult } from '../types/auth'

const TOKEN_KEY = 'staff_console_token'
const LEGACY_TOKEN_KEY = 'admin_token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || localStorage.getItem(LEGACY_TOKEN_KEY) || '',
    user: null as CurrentUser | null,
  }),
  getters: {
    isAdmin: (state) => !!state.user?.roles?.some((role) => role.roleCode === 'ADMIN'),
    isStaff: (state) => !!state.user?.roles?.some((role) => ['ADMIN', 'OPERATOR'].includes(role.roleCode)),
  },
  actions: {
    async login(payload: { username: string; password: string }) {
      const response = await loginApi(payload) as LoginResult
      this.token = response.token
      localStorage.setItem(TOKEN_KEY, response.token)
      localStorage.removeItem(LEGACY_TOKEN_KEY)
      this.user = null
      return response
    },
    async fetchCurrentUser() {
      const user = await getCurrentUser() as CurrentUser
      this.user = user
      return user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(LEGACY_TOKEN_KEY)
    },
  },
})
