import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 60000, // LLM 生成可能需要一些时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器：自动附加 Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：处理 401 错误
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // 如果是 401 且不是刷新请求，尝试刷新 Token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      const refreshToken = localStorage.getItem('refreshToken') || sessionStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const response = await axios.post('/api/v1/auth/refresh', { refreshToken })
          if (response.data.code === 200) {
            const { accessToken, refreshToken: newRefreshToken } = response.data.data
            const storage = localStorage.getItem('refreshToken') ? localStorage : sessionStorage
            storage.setItem('accessToken', accessToken)
            storage.setItem('refreshToken', newRefreshToken)

            originalRequest.headers.Authorization = `Bearer ${accessToken}`
            return api(originalRequest)
          }
        } catch (refreshError) {
          // 刷新失败，清除 Token
          localStorage.removeItem('accessToken')
          localStorage.removeItem('refreshToken')
          sessionStorage.removeItem('accessToken')
          sessionStorage.removeItem('refreshToken')
          window.location.href = '/login'
        }
      }
    }

    return Promise.reject(error)
  }
)

/**
 * 执行占卜
 * @param {string} name - 用户姓名
 * @param {string} matter - 测算事由
 * @returns {Promise<object>} 占卜结果
 */
export async function performDivination(name, matter) {
  const response = await api.post('/divination', { name, matter })
  return response.data
}

/**
 * 健康检查
 * @returns {Promise<object>}
 */
export async function healthCheck() {
  const response = await api.get('/health')
  return response.data
}
