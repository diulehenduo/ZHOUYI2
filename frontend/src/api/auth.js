import axios from 'axios'

// 使用原生 axios，不带拦截器（认证接口不需要 Token）
const API_BASE = '/api/v1/auth'

/**
 * 用户注册
 */
export async function register(username, password, nickname) {
  const response = await axios.post(`${API_BASE}/register`, {
    username,
    password,
    nickname
  })
  return response.data
}

/**
 * 用户登录
 */
export async function login(username, password, rememberMe = false) {
  const response = await axios.post(`${API_BASE}/login`, {
    username,
    password,
    rememberMe
  })
  return response.data
}

/**
 * 刷新 Token
 */
export async function refreshToken(refreshToken) {
  const response = await axios.post(`${API_BASE}/refresh`, {
    refreshToken
  })
  return response.data
}

/**
 * 获取当前用户信息（需要 Token）
 */
export async function getCurrentUser() {
  const token = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken')
  const response = await axios.get(`${API_BASE}/me`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
  return response.data
}
