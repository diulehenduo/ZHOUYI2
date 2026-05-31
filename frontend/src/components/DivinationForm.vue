<template>
  <div class="form-container">
    <div class="form-card">
      <div class="form-header">
        <span class="taiji-icon">☯</span>
        <h2>周易占卜</h2>
        <p class="subtitle">心诚则灵，请专注您想问的事情</p>
      </div>

      <form @submit.prevent="handleSubmit" class="form-body">
        <div class="form-group">
          <label for="name">您的姓名</label>
          <input
            id="name"
            v-model="form.name"
            type="text"
            placeholder="请输入您的姓名"
            maxlength="20"
            class="form-input"
            :disabled="loading"
          />
        </div>

        <div class="form-group">
          <label for="matter">测算事由</label>
          <textarea
            id="matter"
            v-model="form.matter"
            placeholder="请描述您想测算的事情，如：求事业、问姻缘、看财运、近期决策等"
            rows="4"
            maxlength="200"
            class="form-textarea"
            :disabled="loading"
          ></textarea>
          <span class="char-count">{{ form.matter.length }}/200</span>
        </div>

        <button
          type="submit"
          class="submit-btn"
          :disabled="loading || !isValid"
        >
          <span v-if="loading" class="loading-spinner">
            <span class="spin">☯</span>
            摇卦中，请静心等待...
          </span>
          <span v-else>
            <span class="btn-icon">⚘</span>
            开始占卜
          </span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed, ref } from 'vue'

const emit = defineEmits(['submit'])

const form = reactive({
  name: '',
  matter: ''
})

const loading = ref(false)

const isValid = computed(() => {
  return form.name.trim().length > 0 && form.matter.trim().length > 0
})

function handleSubmit() {
  if (!isValid.value || loading.value) return
  loading.value = true
  emit('submit', {
    name: form.name.trim(),
    matter: form.matter.trim(),
    done: () => { loading.value = false }
  })
}

// 暴露 reset 方法供父组件使用
function reset() {
  form.name = ''
  form.matter = ''
}
defineExpose({ reset, loading })
</script>

<style scoped>
.form-container {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.form-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.taiji-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 8px;
}

.form-header h2 {
  font-size: 24px;
  color: #1a1a2e;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #888;
  font-size: 14px;
  margin: 0;
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.form-input,
.form-textarea {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
  font-family: inherit;
  background: #fafafa;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #c0392b;
  background: white;
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #aaa;
}

.submit-btn {
  padding: 14px 24px;
  background: linear-gradient(135deg, #c0392b, #e74c3c);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 8px;
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #a93226, #c0392b);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(192, 57, 43, 0.3);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.spin {
  display: inline-block;
  animation: spin 1s linear infinite;
  font-size: 20px;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.btn-icon {
  margin-right: 4px;
}
</style>
