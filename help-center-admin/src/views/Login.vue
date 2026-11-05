<template>
  <div class="page">
    <form class="card form" @submit.prevent="submit">
      <div class="form-brand">
        <span class="mark">HC</span>
        <div>
          <h1>帮助中心管理</h1>
          <p>登录后处理用户反馈工单</p>
        </div>
      </div>
      <div class="field">
        <span class="field-label">用户名</span>
        <input v-model="username" class="input" autocomplete="username" />
      </div>
      <div class="field">
        <span class="field-label">密码</span>
        <input
          v-model="password"
          class="input"
          type="password"
          autocomplete="current-password"
        />
      </div>
      <p v-if="error" class="alert alert-error">{{ error }}</p>
      <button type="submit" class="btn btn-primary submit" :disabled="loading">
        {{ loading ? '登录中…' : '登录' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '../api/adminAuth.js'
import { TOKEN_KEY } from '../api/request.js'

const router = useRouter()
const route = useRoute()
const username = ref('admin')
const password = ref('admin')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    const { data } = await login(username.value, password.value)
    localStorage.setItem(TOKEN_KEY, data.token)
    const redirect = route.query.redirect || '/'
    router.replace(typeof redirect === 'string' ? redirect : '/')
  } catch (e) {
    error.value =
      e.response?.status === 401 ? '用户名或密码错误' : e.response?.data?.message || e.message
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(145deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
}

.form {
  width: 100%;
  max-width: 400px;
  padding: 36px 32px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-brand {
  display: flex;
  gap: 14px;
  align-items: center;
  margin-bottom: 8px;
}

.mark {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-sm);
  background: var(--primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
}

.form-brand h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.form-brand p {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.submit {
  width: 100%;
  padding: 11px;
  margin-top: 4px;
}
</style>
