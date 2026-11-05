<template>
  <div class="layout">
    <aside class="side">
      <div class="brand">
        <span class="brand-mark">HC</span>
        <div>
          <div class="brand-title">帮助中心</div>
          <div class="brand-sub">反馈工单管理</div>
        </div>
      </div>
      <nav class="nav">
        <router-link to="/" class="nav-item" active-class="active" exact>
          工单列表
        </router-link>
      </nav>
      <button type="button" class="logout btn btn-ghost" @click="logout">退出登录</button>
    </aside>
    <div class="shell">
      <main class="main">
        <slot />
      </main>
    </div>
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
  width: 240px;
  flex-shrink: 0;
  background: var(--sidebar);
  color: #fff;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
}

.brand {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 32px;
  padding: 0 8px;
}

.brand-mark {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-sm);
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.brand-title {
  font-weight: 600;
  font-size: 15px;
}

.brand-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.55);
  margin-top: 2px;
}

.nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: block;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  color: rgba(255, 255, 255, 0.78);
  text-decoration: none;
  font-size: 14px;
  transition: background 0.15s ease, color 0.15s ease;
}

.nav-item:hover {
  background: var(--sidebar-hover);
  color: #fff;
}

.nav-item.active {
  background: var(--sidebar-active);
  color: #fff;
  font-weight: 500;
}

.logout {
  margin-top: auto;
  width: 100%;
  background: transparent;
  border-color: rgba(255, 255, 255, 0.25);
  color: rgba(255, 255, 255, 0.85);
}

.logout:hover {
  background: var(--sidebar-hover);
  border-color: rgba(255, 255, 255, 0.4);
  color: #fff;
}

.shell {
  flex: 1;
  min-width: 0;
}

.main {
  padding: 28px 32px 40px;
  max-width: 1280px;
}
</style>
