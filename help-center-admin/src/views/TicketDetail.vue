<template>
  <div v-if="loading" class="loading">
    <div class="spinner" />
  </div>
  <div v-else-if="ticket" class="detail">
    <header class="page-header detail-head">
      <router-link to="/" class="back">← 返回列表</router-link>
      <div class="head-row">
        <div class="title-line">
          <h1>{{ ticket.ticketNo }}</h1>
          <StatusTag :status="ticket.status" variant="hero" />
        </div>
        <p>{{ formatTime(ticket.createdAt) }} · {{ ticket.userName || ticket.userId }}</p>
      </div>
    </header>

    <div class="grid">
      <div class="col-main">
        <section class="card block">
          <h2 class="block-title">基本信息</h2>
          <dl class="meta">
            <dt>用户 ID</dt>
            <dd>{{ ticket.userId }}</dd>
            <dt>用户名</dt>
            <dd>{{ ticket.userName }}</dd>
            <dt>会话 ID</dt>
            <dd class="mono">{{ ticket.chatId }}</dd>
            <dt>问题分类</dt>
            <dd>{{ ticket.categoryLabel || ticket.category || '—' }}</dd>
            <dt>联系方式</dt>
            <dd>{{ ticket.contact || '—' }}</dd>
            <dt>问题描述</dt>
            <dd class="desc">{{ ticket.description }}</dd>
          </dl>
        </section>

        <section class="card block">
          <h2 class="block-title">附图</h2>
          <p v-if="!ticket.attachments?.length" class="muted">无附图</p>
          <div v-else class="attach-grid">
            <a
              v-for="(src, i) in ticket.attachments"
              :key="i"
              class="attach-item"
              :href="src"
              target="_blank"
              rel="noopener"
            >
              <img :src="src" alt="" />
            </a>
          </div>
        </section>

        <section class="card block">
          <h2 class="block-title">聊天记录</h2>
          <ul v-if="ticket.chatTranscript?.length" class="chat">
            <li
              v-for="(m, i) in ticket.chatTranscript"
              :key="i"
              :class="['chat-item', m.role === 'user' ? 'user' : 'assistant']"
            >
              <div class="chat-meta">
                <span class="chat-role">{{ m.role === 'user' ? '用户' : '助手' }}</span>
                <span class="chat-time">{{ formatTime(m.timestamp) }}</span>
              </div>
              <p class="chat-body">{{ m.content }}</p>
            </li>
          </ul>
          <p v-else class="muted">无聊天记录</p>
        </section>
      </div>

      <aside class="col-side">
        <section class="card block sticky">
          <h2 class="block-title">处理</h2>
          <div class="field">
            <span class="field-label">状态</span>
            <AppSelect v-model="form.status" :options="statusOptions" />
          </div>
          <div class="field">
            <span class="field-label">内部备注</span>
            <textarea v-model="form.internalNote" class="textarea" rows="4" />
          </div>
          <div class="field">
            <span class="field-label">对客回复摘要</span>
            <textarea v-model="form.reply" class="textarea" rows="4" />
          </div>
          <button type="button" class="btn btn-primary save" :disabled="saving" @click="save">
            {{ saving ? '保存中…' : '保存' }}
          </button>
          <p v-if="msg" class="alert alert-success">{{ msg }}</p>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { fetchTicket, patchTicket } from '../api/adminTicket.js'
import StatusTag from '../components/StatusTag.vue'
import AppSelect from '../components/AppSelect.vue'

const statusOptions = [
  { value: 'PENDING', label: '待处理' },
  { value: 'IN_PROGRESS', label: '处理中' },
  { value: 'RESOLVED', label: '已解决' },
  { value: 'CLOSED', label: '已关闭' },
]

const props = defineProps({ id: String })

const ticket = ref(null)
const loading = ref(true)
const saving = ref(false)
const form = ref({ status: '', internalNote: '', reply: '' })
const msg = ref('')

async function load() {
  loading.value = true
  msg.value = ''
  try {
    const { data } = await fetchTicket(props.id)
    ticket.value = data
    form.value = {
      status: data.status,
      internalNote: data.internalNote || '',
      reply: data.reply || '',
    }
  } finally {
    loading.value = false
  }
}

onMounted(load)
watch(() => props.id, load)

async function save() {
  saving.value = true
  msg.value = ''
  try {
    await patchTicket(props.id, form.value)
    msg.value = '已保存'
    await load()
  } finally {
    saving.value = false
  }
}

function formatTime(v) {
  if (!v) return '—'
  return new Date(v).toLocaleString('zh-CN')
}
</script>

<style scoped>
.loading {
  display: flex;
  justify-content: center;
  padding: 80px;
}

.detail-head .back {
  display: inline-block;
  margin-bottom: 12px;
  font-size: 13px;
  text-decoration: none;
}

.detail-head .back:hover {
  text-decoration: underline;
}

.head-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title-line {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.title-line h1 {
  margin: 0;
}

.head-row p {
  margin: 0;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
  align-items: start;
}

@media (max-width: 960px) {
  .grid {
    grid-template-columns: 1fr;
  }

  .sticky {
    position: static;
  }
}

.block {
  padding: 20px 22px;
  margin-bottom: 16px;
}

.block-title {
  margin: 0 0 16px;
  font-size: 15px;
  font-weight: 600;
}

.meta {
  display: grid;
  grid-template-columns: 100px 1fr;
  gap: 12px 16px;
  margin: 0;
  font-size: 14px;
}

.meta dt {
  margin: 0;
  color: var(--text-secondary);
}

.meta dd {
  margin: 0;
}

.mono {
  font-size: 13px;
  word-break: break-all;
}

.desc {
  white-space: pre-wrap;
  line-height: 1.55;
}

.muted {
  margin: 0;
  color: var(--text-muted);
  font-size: 14px;
}

.attach-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
}

.attach-item {
  display: block;
  border-radius: var(--radius-sm);
  overflow: hidden;
  border: 1px solid var(--border);
  transition: box-shadow 0.15s ease, transform 0.15s ease;
}

.attach-item:hover {
  box-shadow: var(--shadow);
  transform: translateY(-1px);
}

.attach-item img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  display: block;
}

.chat {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-item {
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
}

.chat-item.user {
  background: var(--primary-soft);
  border-color: #cce4f7;
}

.chat-item.assistant {
  background: #fafbfc;
}

.chat-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.chat-role {
  font-size: 12px;
  font-weight: 600;
}

.chat-time {
  font-size: 12px;
  color: var(--text-muted);
}

.chat-body {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  white-space: pre-wrap;
}

.sticky {
  position: sticky;
  top: 24px;
}

.field + .field {
  margin-top: 14px;
}

.save {
  width: 100%;
  margin-top: 18px;
}

.alert-success {
  margin: 12px 0 0;
}
</style>
