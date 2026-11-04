<template>
  <div class="login-page">
    <div class="card">
      <h1>帮助中心</h1>
      <p class="hint">模拟登录（演示用）</p>
      <label>用户 ID（10 位数字）</label>
      <input v-model="digits" maxlength="10" inputmode="numeric" placeholder="1234567890" />
      <label>用户名（最多 12 字）</label>
      <input v-model="userName" maxlength="12" placeholder="昵称" />
      <p v-if="error" class="error">{{ error }}</p>
      <button type="button" class="primary" @click="submit">进入帮助中心</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  saveUser,
  getOrCreateChatId,
  formatUserId,
  validateDigits10,
  validateUserName,
} from '../utils/mockUser.js'

const emit = defineEmits(['success'])
const digits = ref('')
const userName = ref('')
const error = ref('')

function submit() {
  error.value = ''
  if (!validateDigits10(digits.value)) {
    error.value = '用户 ID 须为 10 位数字'
    return
  }
  if (!validateUserName(userName.value.trim())) {
    error.value = '用户名必填且不超过 12 个字符'
    return
  }
  const user = {
    userId: formatUserId(digits.value),
    userName: userName.value.trim(),
  }
  saveUser(user)
  getOrCreateChatId(true)
  emit('success', user)
}
</script>

<style scoped>
.login-page {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  padding-top: max(24px, env(safe-area-inset-top));
  padding-bottom: max(24px, env(safe-area-inset-bottom));
}
.card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
h1 {
  margin: 0 0 8px;
  font-size: 22px;
}
.hint {
  margin: 0 0 20px;
  color: #666;
  font-size: 14px;
}
label {
  display: block;
  font-size: 13px;
  color: #444;
  margin-bottom: 6px;
}
input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 14px;
}
.error {
  color: #e53935;
  font-size: 13px;
}
.primary {
  width: 100%;
  border: none;
  background: var(--color-primary);
  color: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-top: 8px;
}
</style>
