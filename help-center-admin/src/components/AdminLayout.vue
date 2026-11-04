<template>
  <div class="layout">
    <aside class="side">
      <div class="brand">帮助中心 · 工单</div>
      <router-link to="/">工单列表</router-link>
      <div class="token">
        <label>Admin Token</label>
        <input v-model="token" type="password" @change="saveToken" />
      </div>
    </aside>
    <main class="main">
      <slot />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { TOKEN_KEY } from '../api/request.js'

const token = ref('')

onMounted(() => {
  token.value = localStorage.getItem(TOKEN_KEY) || 'dev-admin-token'
  if (!localStorage.getItem(TOKEN_KEY)) {
    localStorage.setItem(TOKEN_KEY, token.value)
  }
})

function saveToken() {
  localStorage.setItem(TOKEN_KEY, token.value)
}
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}
.side {
  width: 220px;
  background: #001529;
  color: #fff;
  padding: 20px 16px;
}
.brand {
  font-weight: 600;
  margin-bottom: 24px;
}
a {
  display: block;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  margin-bottom: 12px;
}
.token {
  margin-top: 32px;
  font-size: 12px;
}
.token input {
  width: 100%;
  margin-top: 6px;
  padding: 6px;
}
.main {
  flex: 1;
  padding: 24px;
}
</style>
