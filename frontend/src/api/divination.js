import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 60000, // LLM 生成可能需要一些时间
  headers: {
    'Content-Type': 'application/json'
  }
})

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
