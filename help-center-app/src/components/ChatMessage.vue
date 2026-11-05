<template>
  <div :class="['row', message.role]">
    <ChatAvatar :role="message.role" />
    <div class="content">
      <div class="bubble">
        <div v-if="message.role === 'user'" class="text">
          {{ message.content }}<span v-if="message.streaming" class="cursor">▍</span>
        </div>
        <div v-else class="md-body" v-html="htmlContent"></div>
        <span v-if="message.role !== 'user' && message.streaming" class="cursor inline-cursor">▍</span>
      </div>
      <SourceList v-if="message.sources?.length" :sources="message.sources" />
      <span class="time">{{ timeLabel }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import SourceList from './SourceList.vue'
import ChatAvatar from './ChatAvatar.vue'
import { renderMarkdown } from '../utils/renderMarkdown.js'

const props = defineProps({
  message: { type: Object, required: true },
})

const htmlContent = computed(() => renderMarkdown(props.message.content))

const timeLabel = computed(() => {
  const d = new Date(props.message.timestamp)
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
})
</script>

<style scoped>
.row {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}
.row.user {
  flex-direction: row-reverse;
}
.content {
  max-width: 85%;
  margin-top: var(--chat-bubble-top-offset);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.row.user .content {
  align-items: flex-end;
}
.bubble {
  background: #fff;
  border-radius: 4px 12px 12px 12px;
  padding: 12px 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  font-size: 14px;
  line-height: 1.5;
}
.row.user .text {
  white-space: pre-wrap;
}
.md-body :deep(p) {
  margin: 0 0 0.6em;
}
.md-body :deep(p:last-child),
.md-body :deep(ul:last-child),
.md-body :deep(ol:last-child),
.md-body :deep(blockquote:last-child) {
  margin-bottom: 0;
}
.md-body :deep(p:empty) {
  display: none;
}
.md-body :deep(strong) {
  font-weight: 600;
}
.md-body :deep(ul),
.md-body :deep(ol) {
  margin: 0.4em 0 0.6em;
  padding-left: 1.25em;
}
.md-body :deep(li:last-child) {
  margin-bottom: 0;
}
.md-body :deep(blockquote) {
  margin: 0.6em 0 0;
  padding-left: 10px;
  border-left: 3px solid #ddd;
  color: #555;
}
.inline-cursor {
  display: inline-block;
  vertical-align: text-bottom;
}
.row.user .bubble {
  background: var(--color-primary-user-bubble);
  color: #1a1a1a;
  border-radius: 12px 4px 12px 12px;
}
.time {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: #999;
}
.cursor {
  animation: blink 1s step-end infinite;
}
@keyframes blink {
  50% {
    opacity: 0;
  }
}
</style>
