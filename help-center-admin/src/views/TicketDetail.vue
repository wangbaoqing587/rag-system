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

        <section class="card block">
          <h2 class="block-title">备注信息</h2>
          <p v-if="ticket.internalNote?.trim()" class="record-body">{{ ticket.internalNote }}</p>
          <p v-else class="muted">暂无备注</p>
        </section>

        <section class="card block">
          <h2 class="block-title">回复信息</h2>
          <ul v-if="ticket.replies?.length" class="reply-list">
            <li v-for="(item, i) in ticket.replies" :key="i" class="reply-item">
              <div class="reply-meta">
                <span>#{{ i + 1 }}</span>
                <span v-if="item.createdAt">{{ formatTime(item.createdAt) }}</span>
              </div>
              <p class="record-body">{{ item.content }}</p>
            </li>
          </ul>
          <p v-else class="muted">暂无回复</p>
        </section>
      </div>

      <aside class="col-side sticky">
        <div class="card side-stack">
          <section class="side-panel">
            <h2 class="side-title">状态</h2>
            <AppSelect v-model="statusDraft" :options="statusOptions" />
            <div class="side-foot">
              <button
                type="button"
                class="btn btn-primary btn-sm"
                :disabled="statusSaving || statusDraft === ticket.status"
                @click="saveStatus"
              >
                {{ statusSaving ? '更新中…' : '更新状态' }}
              </button>
              <p v-if="statusError" class="side-hint error">{{ statusError }}</p>
              <p v-else-if="statusMsg" class="side-hint ok">{{ statusMsg }}</p>
            </div>
          </section>

          <section class="side-panel">
            <h2 class="side-title">备注</h2>
            <textarea v-model="noteDraft" class="textarea side-textarea" rows="3" />
            <div class="side-foot">
              <button
                type="button"
                class="btn btn-primary btn-sm"
                :disabled="noteSaving"
                @click="saveNote"
              >
                {{ noteSaving ? '保存中…' : '保存备注' }}
              </button>
              <p v-if="noteError" class="side-hint error">{{ noteError }}</p>
              <p v-else-if="noteMsg" class="side-hint ok">{{ noteMsg }}</p>
            </div>
          </section>

          <section class="side-panel">
            <h2 class="side-title">回复</h2>
            <textarea
              v-model="replyDraft"
              class="textarea side-textarea"
              rows="3"
              placeholder="请输入回复内容..."
            />
            <div class="side-foot">
              <button
                type="button"
                class="btn btn-primary btn-sm"
                :disabled="replySaving || !replyDraft.trim()"
                @click="saveReply"
              >
                {{ replySaving ? '提交中…' : '提交回复' }}
              </button>
              <p v-if="replyError" class="side-hint error">{{ replyError }}</p>
              <p v-else-if="replyMsg" class="side-hint ok">{{ replyMsg }}</p>
            </div>
          </section>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import {
  fetchTicket,
  patchTicketInternalNote,
  patchTicketReply,
  patchTicketStatus,
} from '../api/adminTicket.js'
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
const statusDraft = ref('')
const noteDraft = ref('')
const replyDraft = ref('')

const statusSaving = ref(false)
const statusMsg = ref('')
const statusError = ref('')
const noteSaving = ref(false)
const noteMsg = ref('')
const noteError = ref('')
const replySaving = ref(false)
const replyMsg = ref('')
const replyError = ref('')

function syncDrafts(data) {
  statusDraft.value = data.status
  noteDraft.value = data.internalNote || ''
}

function applyTicket(data, options = {}) {
  const { clearReplyInput = false } = options
  ticket.value = data
  syncDrafts(data)
  if (clearReplyInput) {
    replyDraft.value = ''
  }
}

function clearActionMsgs() {
  statusMsg.value = ''
  statusError.value = ''
  noteMsg.value = ''
  noteError.value = ''
  replyMsg.value = ''
  replyError.value = ''
}

async function load(options = {}) {
  const { showLoading = true, clearMsg = true } = options
  if (clearMsg) {
    clearActionMsgs()
  }
  if (showLoading) {
    loading.value = true
  }
  try {
    const { data } = await fetchTicket(props.id)
    ticket.value = data
    syncDrafts(data)
    replyDraft.value = ''
  } catch (e) {
    if (showLoading) {
      statusError.value = e.response?.data?.message || e.message || '加载失败'
    }
    throw e
  } finally {
    if (showLoading) {
      loading.value = false
    }
  }
}

onMounted(load)
watch(() => props.id, load)

async function saveStatus() {
  statusSaving.value = true
  statusMsg.value = ''
  statusError.value = ''
  try {
    const { data } = await patchTicketStatus(props.id, { status: statusDraft.value })
    applyTicket(data)
    statusMsg.value = '状态已更新'
  } catch (e) {
    statusError.value = e.response?.data?.message || e.message || '更新失败'
  } finally {
    statusSaving.value = false
  }
}

async function saveNote() {
  noteSaving.value = true
  noteMsg.value = ''
  noteError.value = ''
  try {
    const { data } = await patchTicketInternalNote(props.id, { internalNote: noteDraft.value })
    applyTicket(data)
    noteMsg.value = '备注已保存'
  } catch (e) {
    noteError.value = e.response?.data?.message || e.message || '保存失败'
  } finally {
    noteSaving.value = false
  }
}

async function saveReply() {
  replySaving.value = true
  replyMsg.value = ''
  replyError.value = ''
  try {
    const { data } = await patchTicketReply(props.id, { reply: replyDraft.value.trim() })
    applyTicket(data, { clearReplyInput: true })
    replyMsg.value = '回复已提交'
  } catch (e) {
    replyError.value = e.response?.data?.message || e.message || '提交失败'
  } finally {
    replySaving.value = false
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
  grid-template-columns: 1fr 260px;
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

.record-body {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.55;
  font-size: 14px;
  color: var(--text);
}

.reply-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-item {
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: #fafbfc;
}

.reply-meta {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 12px;
  color: var(--text-muted);
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

.side-stack {
  overflow: hidden;
  padding: 0;
}

.side-panel {
  padding: 16px 16px 14px;
  border-bottom: 1px solid var(--border);
}

.side-panel:last-child {
  border-bottom: none;
}

.side-title {
  margin: 0 0 10px;
  padding-left: 10px;
  border-left: 3px solid var(--primary);
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
  line-height: 1.2;
}

.side-textarea {
  min-height: 72px;
  font-size: 13px;
}

.side-foot {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}

.side-hint {
  margin: 0;
  width: 100%;
  text-align: center;
  font-size: 12px;
  line-height: 1.4;
}

.side-hint.ok {
  color: var(--success);
}

.side-hint.error {
  color: var(--danger);
}
</style>
