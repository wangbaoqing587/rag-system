<template>
  <div class="welcome">
    <ChatAvatar role="assistant" />
    <div class="body">
      <div class="bubble">
        <p class="lead">你好，我是帮助中心智能助手。</p>
        <p>可解答功能用法、计费与常见故障；未命中文档时可提交工单由人工跟进。</p>
      </div>
      <span class="time">{{ timeLabel }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ChatAvatar from './ChatAvatar.vue'

const props = defineProps({
  timestamp: { type: String, required: true },
})

const timeLabel = computed(() => formatTime(props.timestamp))

function formatTime(iso) {
  const d = new Date(iso)
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.welcome {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}
.body {
  max-width: 85%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.bubble {
  width: 100%;
  background: #fff;
  border-radius: 4px 12px 12px 12px;
  padding: 12px 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  font-size: 14px;
  line-height: 1.5;
}
.lead {
  margin: 0 0 6px;
  font-weight: 500;
}
p {
  margin: 0;
  color: #444;
}
.time {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: #999;
}
</style>
