<template>
  <div class="input-bar">
    <div class="input-row">
      <div class="textarea-shell">
        <textarea
          ref="textareaRef"
          v-model="text"
          rows="1"
          placeholder="描述你的问题…"
          :disabled="disabled"
          @input="resizeTextarea"
          @keydown.enter.exact.prevent="onEnter"
        />
      </div>
      <button
        type="button"
        class="send"
        :class="{ 'is-stop': streaming }"
        :aria-label="streaming ? '停止' : '发送'"
        :disabled="!streaming && (disabled || !text.trim())"
        @click="onAction"
      >
        <svg v-if="streaming" class="action-icon" viewBox="0 0 1024 1024" aria-hidden="true">
          <path
            d="M258.875 174.5h506.25c46.58203125 0 84.375 37.79296875 84.375 84.375v506.25c0 46.58203125-37.79296875 84.375-84.375 84.375H258.875c-46.58203125 0-84.375-37.79296875-84.375-84.375V258.875c0-46.58203125 37.79296875-84.375 84.375-84.375z"
          />
        </svg>
        <svg v-else class="action-icon" viewBox="0 0 1024 1024" aria-hidden="true">
          <path
            d="M926.165333 97.834667a42.666667 42.666667 0 0 1 10.112 44.266666l-268.8 768a42.666667 42.666667 0 0 1-79.274666 3.221334l-146.922667-330.602667-330.602667-146.922667a42.666667 42.666667 0 0 1 3.221334-79.274666l768-268.8a42.666667 42.666667 0 0 1 44.266666 10.112zM243.669333 401.493333l247.253334 109.888a42.666667 42.666667 0 0 1 21.674666 21.674667l109.866667 247.253333L826.453333 197.546667 243.669333 401.493333z"
          />
          <path
            d="M883.498667 140.501333a42.666667 42.666667 0 0 1 0 60.330667l-362.666667 362.666667a42.666667 42.666667 0 1 1-60.330667-60.330667l362.666667-362.666667a42.666667 42.666667 0 0 1 60.330667 0z"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref, watch } from 'vue'

const props = defineProps({
  disabled: Boolean,
  streaming: Boolean,
})

const emit = defineEmits(['send', 'stop'])
const text = ref('')
const textareaRef = ref(null)

const LINE_HEIGHT = 22
const MAX_LINES = 3

function resizeTextarea() {
  const el = textareaRef.value
  if (!el) return
  el.style.height = 'auto'
  const maxBody = LINE_HEIGHT * MAX_LINES
  el.style.height = `${Math.min(el.scrollHeight, maxBody)}px`
  el.style.overflowY = el.scrollHeight > maxBody ? 'auto' : 'hidden'
}

function send() {
  const v = text.value.trim()
  if (!v) return
  emit('send', v)
  text.value = ''
  nextTick(resizeTextarea)
}

function onAction() {
  if (props.streaming) {
    emit('stop')
    return
  }
  send()
}

function onEnter() {
  if (props.streaming) return
  send()
}

onMounted(() => {
  resizeTextarea()
})

watch(text, () => {
  nextTick(resizeTextarea)
})

defineExpose({
  setText(v) {
    text.value = v
    nextTick(resizeTextarea)
  },
})
</script>

<style scoped>
.input-bar {
  background: transparent;
  padding: 0;
}
.input-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.textarea-shell {
  flex: 1;
  min-height: 44px;
  max-height: 88px;
  background: #f0f2f5;
  border-radius: 8px;
  padding: 11px 12px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
}
textarea {
  width: 100%;
  border: none;
  background: transparent;
  border-radius: 0;
  padding: 0;
  resize: none;
  outline: none;
  display: block;
  box-sizing: border-box;
  font-size: 14px;
  line-height: 22px;
  min-height: 22px;
  max-height: 66px;
  overflow-y: hidden;
}
textarea:focus {
  outline: none;
}
textarea::placeholder {
  color: #b0b3b8;
}
.send {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  padding: 0;
  border: 1px solid #e0e0e0;
  background: #fff;
  border-radius: 12px;
  flex-shrink: 0;
  transition: background 0.15s ease, border-color 0.15s ease;
}
.send:not(:disabled):not(.is-stop) {
  background: var(--color-primary);
  border-color: var(--color-primary);
}
.send:disabled {
  cursor: not-allowed;
}
.send.is-stop {
  background: #fff;
  border-color: #e0e0e0;
  cursor: pointer;
}
.action-icon {
  width: 28px;
  height: 28px;
  display: block;
  flex-shrink: 0;
}
.send:not(:disabled):not(.is-stop) .action-icon path {
  fill: #ffffff;
}
.send:disabled .action-icon path {
  fill: #000000;
}
.send.is-stop .action-icon path {
  fill: var(--color-primary);
}
</style>
