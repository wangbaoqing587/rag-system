<template>
  <div class="panel">
    <div class="toolbar">
      <select v-model="status" @change="load">
        <option value="">全部状态</option>
        <option value="PENDING">待处理</option>
        <option value="IN_PROGRESS">处理中</option>
        <option value="RESOLVED">已解决</option>
        <option value="CLOSED">已关闭</option>
      </select>
      <input v-model="keyword" placeholder="工单号 / 描述 / 联系方式 / userId" @keyup.enter="load" />
      <button type="button" @click="load">查询</button>
    </div>
    <table>
      <thead>
        <tr>
          <th>工单号</th>
          <th>用户</th>
          <th>分类</th>
          <th>描述</th>
          <th>联系方式</th>
          <th>状态</th>
          <th>创建时间</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in rows" :key="row.id">
          <td>{{ row.ticketNo }}</td>
          <td>{{ row.userId }}<br /><small>{{ row.userName }}</small></td>
          <td>{{ row.categoryLabel || row.category || '—' }}</td>
          <td class="desc">{{ row.description }}</td>
          <td>{{ row.contact || '—' }}</td>
          <td><StatusTag :status="row.status" /></td>
          <td>{{ formatTime(row.createdAt) }}</td>
          <td><router-link :to="`/tickets/${row.id}`">详情</router-link></td>
        </tr>
      </tbody>
    </table>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchTickets } from '../api/adminTicket.js'
import StatusTag from '../components/StatusTag.vue'

const rows = ref([])
const status = ref('')
const keyword = ref('')
const error = ref('')

onMounted(load)

async function load() {
  error.value = ''
  try {
    const { data } = await fetchTickets({
      page: 0,
      size: 50,
      status: status.value || undefined,
      keyword: keyword.value || undefined,
    })
    rows.value = data.content || []
  } catch (e) {
    error.value = e.response?.status === 401 ? '未授权，请重新登录' : e.message
  }
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
  padding: 16px;
}
.toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
input,
select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
th,
td {
  border-bottom: 1px solid #eee;
  padding: 10px 8px;
  text-align: left;
  vertical-align: top;
}
.desc {
  max-width: 320px;
}
.error {
  color: #c00;
}
</style>
