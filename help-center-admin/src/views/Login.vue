<template>
  <div class="page">
    <form class="card" @submit.prevent="submit">
      <h1>帮助中心 · 管理登录</h1>
      <label>
        用户名
        <input v-model="username" autocomplete="username" />
      </label>
      <label>
        密码
        <input v-model="password" type="password" autocomplete="current-password" />
      </label>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="loading">{{ loading ? '登录中…' : '登录' }}</button>
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
  background: #001529;
}
.card {
  width: 320px;
  padding: 32px 28px;
  background: #fff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
h1 {
  margin: 0 0 8px;
  font-size: 18px;
  text-align: center;
}
label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}
input {
  padding: 8px 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}
button {
  margin-top: 8px;
  padding: 10px;
  background: #0071e3;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.error {
  margin: 0;
  color: #cf1322;
  font-size: 13px;
}
</style>
