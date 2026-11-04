<template>
  <header class="header">
    <div class="brand">
      <div class="app-icon" aria-hidden="true">
        <img :src="icHelp" alt="" />
      </div>
      <h1 class="title">帮助中心</h1>
    </div>
    <div class="actions">
      <div class="user-meta">
        <span class="user-name">{{ user.userName }}</span>
        <span class="user-id">{{ user.userId }}</span>
      </div>
      <span class="divider" aria-hidden="true" />
      <button type="button" class="switch" @click="$emit('switch-user')">切换用户</button>
    </div>
  </header>
</template>

<script setup>
import icHelp from '../assets/ic_help.svg'

defineProps({
  user: { type: Object, required: true },
})
defineEmits(['switch-user'])
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  min-height: 52px;
  padding: 10px 16px;
  padding-top: max(10px, env(safe-area-inset-top));
  background: #fff;
  border-bottom: 1px solid #ebebeb;
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex-shrink: 1;
}
.app-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.app-icon img {
  width: 22px;
  height: 22px;
  display: block;
  object-fit: contain;
  filter: brightness(0) invert(1);
}
.title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #1a1a1a;
}
.actions {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}
.user-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  min-width: 0;
}
.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.2;
  max-width: 96px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.user-id {
  font-size: 11px;
  color: #999;
  line-height: 1.2;
}
.divider {
  width: 1px;
  height: 20px;
  background: #e8e8e8;
  flex-shrink: 0;
}
.switch {
  border: none;
  background: none;
  padding: 0;
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
}
</style>
