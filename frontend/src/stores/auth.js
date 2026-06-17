import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister, refreshToken as apiRefreshToken, getCurrentUser } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref(null)
  const accessToken = ref(localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken') || '')
  const refreshTokenValue = ref(localStorage.getItem('refreshToken') || sessionStorage.getItem('refreshToken') || '')

  // 计算属性
  const isAuthenticated = computed(() => !!accessToken.value)
  const username = computed(() => user.value?.username || '')
  const nickname = computed(() => user.value?.nickname || '')

  // 设置 Token
  function setTokens(access, refresh, rememberMe) {
    accessToken.value = access
    refreshTokenValue.value = refresh

    if (rememberMe) {
      localStorage.setItem('accessToken', access)
      localStorage.setItem('refreshToken', refresh)
    } else {
      sessionStorage.setItem('accessToken', access)
      sessionStorage.setItem('refreshToken', refresh)
    }
  }

  // 清除 Token
  function clearTokens() {
    accessToken.value = ''
    refreshTokenValue.value = ''
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    sessionStorage.removeItem('accessToken')
    sessionStorage.removeItem('refreshToken')
  }

  // 登录
  async function login(username, password, rememberMe = false) {
    try {
      const response = await apiLogin(username, password, rememberMe)
      if (response.code === 200) {
        setTokens(response.data.accessToken, response.data.refreshToken, rememberMe)
        user.value = {
          username: response.data.username,
          nickname: response.data.nickname
        }
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '登录失败' }
    }
  }

  // 注册
  async function register(username, password, nickname) {
    try {
      const response = await apiRegister(username, password, nickname)
      if (response.code === 200) {
        setTokens(response.data.accessToken, response.data.refreshToken, false)
        user.value = {
          username: response.data.username,
          nickname: response.data.nickname
        }
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '注册失败' }
    }
  }

  // 刷新 Token
  async function refresh() {
    try {
      if (!refreshTokenValue.value) {
        throw new Error('无 Refresh Token')
      }
      const response = await apiRefreshToken(refreshTokenValue.value)
      if (response.code === 200) {
        setTokens(response.data.accessToken, response.data.refreshToken, !!localStorage.getItem('refreshToken'))
        return true
      } else {
        clearTokens()
        return false
      }
    } catch (error) {
      clearTokens()
      return false
    }
  }

  // 获取用户信息
  async function fetchUser() {
    try {
      const response = await getCurrentUser()
      if (response.code === 200) {
        user.value = response.data
        return true
      }
      return false
    } catch (error) {
      return false
    }
  }

  // 登出
  function logout() {
    clearTokens()
  }

  return {
    user,
    accessToken,
    isAuthenticated,
    username,
    nickname,
    login,
    register,
    refresh,
    fetchUser,
    logout,
    clearTokens
  }
})
