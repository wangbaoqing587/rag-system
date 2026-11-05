<template>
  <div v-if="visible" class="mask" @click.self="close">
    <div class="sheet">
      <h3>提交反馈</h3>
      <label>问题分类</label>
      <select v-model="category">
        <option value="">请选择</option>
        <option v-for="c in TICKET_CATEGORIES" :key="c.value" :value="c.value">
          {{ c.label }}
        </option>
      </select>

      <label>问题描述</label>
      <textarea v-model="description" rows="4" placeholder="请描述遇到的问题" />

      <label>联系方式</label>
      <input v-model="contact" type="text" placeholder="手机号或邮箱" maxlength="64" />

      <label>附图（最多 4 张）</label>
      <div class="images">
        <div v-for="(img, idx) in images" :key="idx" class="thumb">
          <img :src="img" alt="" />
          <button type="button" class="remove" @click="removeImage(idx)">×</button>
        </div>
        <label v-if="images.length < 4" class="add">
          <input type="file" accept="image/*" multiple @change="onPickImages" />
          <span>+</span>
        </label>
      </div>

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
import { TICKET_CATEGORIES } from '../constants/ticketCategories.js'

const props = defineProps({
  visible: Boolean,
  user: Object,
  chatId: String,
  suggestedDescription: String,
  chatTranscript: Array,
})

const emit = defineEmits(['close', 'success'])

const category = ref('')
const description = ref('')
const contact = ref('')
const images = ref([])
const error = ref('')
const submitting = ref(false)

watch(
  () => props.visible,
  (v) => {
    if (v) {
      category.value = ''
      description.value =
        props.suggestedDescription ||
        props.chatTranscript?.filter((m) => m.role === 'user').pop()?.content ||
        ''
      contact.value = ''
      images.value = []
      error.value = ''
    }
  }
)

function close() {
  emit('close')
}

function removeImage(idx) {
  images.value.splice(idx, 1)
}

function onPickImages(e) {
  const files = Array.from(e.target.files || [])
  e.target.value = ''
  const remain = 4 - images.value.length
  if (remain <= 0) return
  const picked = files.slice(0, remain)
  for (const file of picked) {
    if (!file.type.startsWith('image/')) continue
    const reader = new FileReader()
    reader.onload = () => {
      if (images.value.length < 4 && typeof reader.result === 'string') {
        images.value.push(reader.result)
      }
    }
    reader.readAsDataURL(file)
  }
}

async function submit() {
  if (!category.value) {
    error.value = '请选择问题分类'
    return
  }
  if (!description.value.trim()) {
    error.value = '请填写问题描述'
    return
  }
  if (!contact.value.trim()) {
    error.value = '请填写联系方式'
    return
  }
  submitting.value = true
  error.value = ''
  try {
    const { data } = await createTicket({
      userId: props.user.userId,
      userName: props.user.userName,
      chatId: props.chatId,
      category: category.value,
      description: description.value.trim(),
      contact: contact.value.trim(),
      attachments: images.value,
      chatTranscript: props.chatTranscript,
    })
    emit('success', data)
    close()
  } catch (e) {
    error.value = e.response?.data?.message || e.message || '提交失败'
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
  max-height: 90vh;
  overflow-y: auto;
  background: #fff;
  border-radius: 16px 16px 0 0;
  padding: 20px 16px;
  padding-bottom: max(20px, env(safe-area-inset-bottom));
}
h3 {
  margin: 0 0 16px;
  text-align: center;
}
label {
  display: block;
  margin-top: 12px;
  font-size: 13px;
  color: #666;
}
select,
input,
textarea {
  width: 100%;
  margin-top: 8px;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 10px;
  box-sizing: border-box;
  font-size: 14px;
}
.images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
}
.thumb,
.add {
  width: 72px;
  height: 72px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
}
.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.remove {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 22px;
  height: 22px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  line-height: 1;
  padding: 0;
}
.add {
  border: 1px dashed #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 28px;
}
.add input {
  display: none;
}
.error {
  color: #e53935;
  font-size: 13px;
  margin-top: 12px;
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
