<template>
  <div class="row">
    <ChatAvatar role="assistant" />
    <div class="content">
      <div class="bubble">
        <p>{{ text }}</p>
        <button type="button" class="cta" @click="$emit('submit-ticket')">提交反馈</button>
      </div>
      <span class="time">{{ timeLabel }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ChatAvatar from './ChatAvatar.vue'

const props = defineProps({
  text: {
    type: String,
    default: '抱歉，我暂时给不了更合适的指引。如果需要人工协助，提交反馈后客服会继续跟进。',
  },
  timestamp: { type: String, required: true },
})

defineEmits(['submit-ticket'])

const timeLabel = computed(() => {
  const d = new Date(props.timestamp)
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
})
</script>

<style scoped>
.row {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}
.content {
  max-width: 85%;
  margin-top: var(--chat-bubble-top-offset);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.bubble {
  background: #fff;
  border-radius: 4px 12px 12px 12px;
  padding: 12px 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  font-size: 14px;
}
p {
  margin: 0 0 12px;
  line-height: 1.5;
}
.cta {
  width: 100%;
  border: none;
  background: var(--color-primary-soft);
  color: var(--color-primary);
  border-radius: 10px;
  padding: 12px;
  font-weight: 500;
}
.time {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: #999;
}
</style>
