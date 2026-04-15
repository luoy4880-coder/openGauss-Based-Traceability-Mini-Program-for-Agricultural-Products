import { defineStore } from 'pinia'
import { getCurrentUser, loginApi } from '../api/modules/auth'
import type { CurrentUser, LoginResult } from '../types/auth'

const TOKEN_KEY = 'admin_token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: null as CurrentUser | null,
  }),
  actions: {
    async login(payload: { username: string; password: string }) {
      const response = await loginApi(payload) as LoginResult
      this.token = response.token
      localStorage.setItem(TOKEN_KEY, response.token)
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
    },
  },
})
