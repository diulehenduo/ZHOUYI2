<template>
  <div class="home">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-brand">☯ 周易八卦</div>
      <div class="nav-user">
        <span class="user-info">{{ authStore.nickname || authStore.username }}</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>
    </nav>

    <!-- 主内容 -->
    <div class="app">
      <!-- 头部 -->
      <header class="app-header">
        <h1>周易八卦</h1>
        <p class="subtitle">《周易》占卜服务平台 · 心诚则灵</p>
        <div class="divider"></div>
      </header>

      <!-- 表单 -->
      <DivinationForm ref="formRef" @submit="handleDivination" />

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-overlay">
        <div class="loading-content">
          <span class="loading-icon">☯</span>
          <p>摇卦中，请静心等待...</p>
        </div>
      </div>

      <!-- 错误提示 -->
      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <!-- 结果 -->
      <Transition name="fade">
        <HexagramResult v-if="result" :result="result" />
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import DivinationForm from '../components/DivinationForm.vue'
import HexagramResult from '../components/HexagramResult.vue'
import { performDivination } from '../api/divination.js'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref(null)
const result = ref(null)
const loading = ref(false)
const error = ref('')

async function handleDivination({ name, matter, done }) {
  error.value = ''
  result.value = null
  loading.value = true

  try {
    const response = await performDivination(name, matter)
    if (response.code === 200 && response.data) {
      result.value = response.data
    } else {
      error.value = response.message || '占卜失败，请稍后重试'
    }
  } catch (err) {
    console.error('占卜请求失败:', err)
    if (err.response) {
      if (err.response.status === 401) {
        error.value = '登录已过期，请重新登录'
        authStore.logout()
        router.push('/login')
      } else {
        error.value = `服务器错误 (${err.response.status}): ${err.response.data?.message || '请稍后重试'}`
      }
    } else if (err.request) {
      error.value = '无法连接服务器，请确认后端服务已启动'
    } else {
      error.value = '请求失败: ' + err.message
    }
  } finally {
    loading.value = false
    if (done) done()
  }
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.nav-brand {
  color: #d4af37;
  font-size: 24px;
  font-weight: 600;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  color: #c9b8ff;
  font-size: 14px;
}

.logout-btn {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.app {
  padding: 40px 20px 60px;
  max-width: 800px;
  margin: 0 auto;
}

.loading-overlay {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.loading-content {
  text-align: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px 60px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.2);
}

.loading-icon {
  display: inline-block;
  font-size: 48px;
  animation: spin 1.5s ease-in-out infinite;
  margin-bottom: 16px;
}

.loading-content p {
  color: #c9b8ff;
  font-size: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
