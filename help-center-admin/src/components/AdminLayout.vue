<template>
  <div class="layout">
    <aside class="side">
      <div class="brand">帮助中心 · 工单</div>
      <router-link to="/">工单列表</router-link>
      <button type="button" class="logout" @click="logout">退出登录</button>
    </aside>
    <main class="main">
      <slot />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { TOKEN_KEY } from '../api/request.js'

const router = useRouter()

function logout() {
  localStorage.removeItem(TOKEN_KEY)
  router.push('/login')
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
.logout {
  margin-top: 32px;
  width: 100%;
  padding: 8px;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.35);
  color: rgba(255, 255, 255, 0.85);
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}
.main {
  flex: 1;
  padding: 24px;
}
</style>
