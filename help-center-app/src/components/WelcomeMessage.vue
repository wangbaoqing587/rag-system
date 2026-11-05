<template>
  <div class="welcome">
    <ChatAvatar role="assistant" />
    <div class="body">
      <div class="bubble">
        <p class="lead">{{ WELCOME_INTRO }}</p>
        <p>{{ WELCOME_SCOPE }}</p>
        <p>{{ WELCOME_GUIDE }}</p>
      </div>
      <span class="time">{{ timeLabel }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ChatAvatar from './ChatAvatar.vue'
import { WELCOME_INTRO, WELCOME_SCOPE, WELCOME_GUIDE } from '../constants/welcomeCopy.js'

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
  margin-top: var(--chat-bubble-top-offset);
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
  margin: 0;
  font-weight: 500;
  color: #444;
}
p {
  margin: 0;
  color: #444;
}
.bubble p + p {
  margin-top: 6px;
}
.time {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: #999;
}
</style>
