<template>
  <div class="register-container">
    <div class="register-card">
      <h2>注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="请输入用户名（3-50个字符）"
            required
          />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="请输入密码（至少6位）"
            required
          />
        </div>
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            required
          />
        </div>
        <div class="form-group">
          <label for="nickname">昵称（可选）</label>
          <input
            id="nickname"
            v-model="nickname"
            type="text"
            placeholder="请输入昵称"
          />
        </div>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <button type="submit" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <div class="login-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const nickname = ref('')
const loading = ref(false)
const errorMessage = ref('')

async function handleRegister() {
  loading.value = true
  errorMessage.value = ''

  // 验证密码
  if (password.value !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致'
    loading.value = false
    return
  }

  if (password.value.length < 6) {
    errorMessage.value = '密码长度至少6位'
    loading.value = false
    return
  }

  const result = await authStore.register(
    username.value,
    password.value,
    nickname.value || username.value
  )

  if (result.success) {
    router.push('/')
  } else {
    errorMessage.value = result.message
  }

  loading.value = false
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.register-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

h2 {
  text-align: center;
  color: #d4af37;
  margin-bottom: 30px;
  font-size: 28px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  color: #c9b8ff;
  margin-bottom: 8px;
  font-size: 14px;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 16px;
  transition: border-color 0.3s;
}

input[type="text"]:focus,
input[type="password"]:focus {
  outline: none;
  border-color: #d4af37;
}

.error-message {
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 16px;
  text-align: center;
}

button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #d4af37 0%, #f0d060 100%);
  color: #1a1a2e;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(212, 175, 55, 0.4);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #c9b8ff;
}

.login-link a {
  color: #d4af37;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
