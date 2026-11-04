<template>
  <div v-if="visible" class="mask" @click.self="close">
    <div class="sheet">
      <h3>提交工单</h3>
      <label>问题描述</label>
      <textarea v-model="description" rows="4" />
      <p v-if="error" class="error">{{ error }}</p>
      <div class="footer">
        <button type="button" @click="close">取消</button>
        <button type="button" class="primary" :disabled="submitting" @click="submit">
          {{ submitting ? '提交中…' : '确认提交' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { createTicket } from '../api/ticket.js'

const props = defineProps({
  visible: Boolean,
  user: Object,
  chatId: String,
  suggestedDescription: String,
  chatTranscript: Array,
})

const emit = defineEmits(['close', 'success'])

const description = ref('')
const error = ref('')
const submitting = ref(false)

watch(
  () => props.visible,
  (v) => {
    if (v) {
      description.value =
        props.suggestedDescription ||
        props.chatTranscript?.filter((m) => m.role === 'user').pop()?.content ||
        ''
      error.value = ''
    }
  }
)

function close() {
  emit('close')
}

async function submit() {
  if (!description.value.trim()) {
    error.value = '请填写问题描述'
    return
  }
  submitting.value = true
  error.value = ''
  try {
    const { data } = await createTicket({
      userId: props.user.userId,
      userName: props.user.userName,
      chatId: props.chatId,
      description: description.value.trim(),
      chatTranscript: props.chatTranscript,
    })
    emit('success', data)
    close()
  } catch (e) {
    error.value = e.message || '提交失败'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: flex-end;
  z-index: 100;
}
.sheet {
  width: 100%;
  background: #fff;
  border-radius: 16px 16px 0 0;
  padding: 20px 16px;
  padding-bottom: max(20px, env(safe-area-inset-bottom));
}
h3 {
  margin: 0 0 16px;
}
label {
  font-size: 13px;
  color: #666;
}
textarea {
  width: 100%;
  margin-top: 8px;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 10px;
}
.error {
  color: #e53935;
  font-size: 13px;
}
.footer {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}
.footer button {
  flex: 1;
  padding: 12px;
  border-radius: 10px;
  border: 1px solid #ddd;
  background: #fff;
}
.primary {
  background: var(--color-primary) !important;
  color: #fff;
  border: none !important;
}
</style>
