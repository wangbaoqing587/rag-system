<template>
  <div v-if="ticket" class="panel">
    <router-link to="/">← 返回列表</router-link>
    <h2>{{ ticket.ticketNo }}</h2>
    <p><StatusTag :status="ticket.status" /></p>
    <dl>
      <dt>用户 ID</dt>
      <dd>{{ ticket.userId }}</dd>
      <dt>用户名</dt>
      <dd>{{ ticket.userName }}</dd>
      <dt>chatId</dt>
      <dd>{{ ticket.chatId }}</dd>
      <dt>问题描述</dt>
      <dd>{{ ticket.description }}</dd>
      <dt>创建时间</dt>
      <dd>{{ formatTime(ticket.createdAt) }}</dd>
    </dl>
    <h3>聊天记录</h3>
    <ul class="timeline">
      <li v-for="(m, i) in ticket.chatTranscript" :key="i">
        <span class="role">{{ m.role === 'user' ? '用户' : '助手' }}</span>
        <span class="time">{{ formatTime(m.timestamp) }}</span>
        <p>{{ m.content }}</p>
      </li>
    </ul>
    <h3>处理</h3>
    <label>状态</label>
    <select v-model="form.status">
      <option value="PENDING">待处理</option>
      <option value="IN_PROGRESS">处理中</option>
      <option value="RESOLVED">已解决</option>
      <option value="CLOSED">已关闭</option>
    </select>
    <label>内部备注</label>
    <textarea v-model="form.internalNote" rows="3" />
    <label>对客回复摘要</label>
    <textarea v-model="form.reply" rows="3" />
    <button type="button" @click="save">保存</button>
    <p v-if="msg" class="msg">{{ msg }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { fetchTicket, patchTicket } from '../api/adminTicket.js'
import StatusTag from '../components/StatusTag.vue'

const props = defineProps({ id: String })

const ticket = ref(null)
const form = ref({ status: '', internalNote: '', reply: '' })
const msg = ref('')

async function load() {
  const { data } = await fetchTicket(props.id)
  ticket.value = data
  form.value = {
    status: data.status,
    internalNote: data.internalNote || '',
    reply: data.reply || '',
  }
}

onMounted(load)
watch(() => props.id, load)

async function save() {
  await patchTicket(props.id, form.value)
  msg.value = '已保存'
  await load()
}

function formatTime(v) {
  if (!v) return ''
  return new Date(v).toLocaleString('zh-CN')
}
</script>

<style scoped>
.panel {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  max-width: 900px;
}
dl {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 8px;
}
dt {
  color: #666;
}
.timeline {
  list-style: none;
  padding: 0;
}
.timeline li {
  border-left: 3px solid #1677ff;
  padding-left: 12px;
  margin-bottom: 12px;
}
.role {
  font-weight: 600;
  margin-right: 8px;
}
.time {
  font-size: 12px;
  color: #999;
}
label {
  display: block;
  margin-top: 12px;
  font-size: 13px;
}
select,
textarea {
  width: 100%;
  max-width: 560px;
  margin-top: 4px;
  padding: 8px;
}
button {
  margin-top: 16px;
  padding: 8px 24px;
}
.msg {
  color: #389e0d;
}
</style>
