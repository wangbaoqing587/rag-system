<template>
  <div>
    <header class="page-header">
      <h1>工单列表</h1>
      <p>查看并处理用户提交的反馈工单</p>
    </header>

    <div class="card panel">
      <div class="toolbar">
        <div class="field toolbar-field">
          <span class="field-label">状态</span>
          <AppSelect v-model="status" :options="statusOptions" @change="search" />
        </div>
        <div class="field toolbar-search">
          <span class="field-label">搜索</span>
          <input
            v-model="keyword"
            class="input"
            placeholder="工单号、描述、联系方式、用户 ID"
            @keyup.enter="search"
          />
        </div>
        <button type="button" class="btn btn-primary toolbar-btn" :disabled="loading" @click="search">
          查询
        </button>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner" />
      </div>

      <div v-else-if="error" class="alert alert-error table-wrap">{{ error }}</div>

      <div v-else-if="!rows.length" class="empty">暂无工单，或当前筛选条件下无结果</div>

      <div v-else class="table-wrap">
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
              <td class="mono">{{ row.ticketNo }}</td>
              <td>{{ row.userName || '—' }}</td>
              <td>{{ row.categoryLabel || row.category || '—' }}</td>
              <td class="desc">{{ row.description }}</td>
              <td>{{ row.contact || '—' }}</td>
              <td><StatusTag :status="row.status" /></td>
              <td class="time">{{ formatTime(row.createdAt) }}</td>
              <td>
                <router-link class="link-detail" :to="`/tickets/${row.id}`">详情</router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <footer v-if="!loading && !error && totalElements > 0" class="table-foot pager">
        <span class="pager-total">共 {{ totalElements }} 条</span>
        <div class="pager-actions">
          <label class="pager-size">
            每页
            <select v-model.number="pageSize" class="select" @change="onPageSizeChange">
              <option :value="10">10</option>
              <option :value="20">20</option>
              <option :value="50">50</option>
            </select>
          </label>
          <button
            type="button"
            class="btn btn-ghost pager-btn"
            :disabled="page <= 0"
            @click="goPage(page - 1)"
          >
            上一页
          </button>
          <span class="pager-num">{{ page + 1 }} / {{ totalPages }}</span>
          <button
            type="button"
            class="btn btn-ghost pager-btn"
            :disabled="page >= totalPages - 1"
            @click="goPage(page + 1)"
          >
            下一页
          </button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchTickets } from '../api/adminTicket.js'
import StatusTag from '../components/StatusTag.vue'
import AppSelect from '../components/AppSelect.vue'

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'PENDING', label: '待处理' },
  { value: 'IN_PROGRESS', label: '处理中' },
  { value: 'RESOLVED', label: '已解决' },
  { value: 'CLOSED', label: '已关闭' },
]

const rows = ref([])
const status = ref('')
const keyword = ref('')
const error = ref('')
const loading = ref(false)
const page = ref(0)
const pageSize = ref(20)
const totalElements = ref(0)
const totalPages = ref(0)

onMounted(load)

function search() {
  page.value = 0
  load()
}

function goPage(p) {
  page.value = p
  load()
}

function onPageSizeChange() {
  page.value = 0
  load()
}

async function load() {
  error.value = ''
  loading.value = true
  try {
    const { data } = await fetchTickets({
      page: page.value,
      size: pageSize.value,
      status: status.value || undefined,
      keyword: keyword.value || undefined,
    })
    rows.value = data.content || []
    totalElements.value = data.totalElements ?? 0
    totalPages.value = Math.max(1, data.totalPages ?? 1)
    if (page.value >= totalPages.value && totalPages.value > 0) {
      page.value = totalPages.value - 1
      loading.value = false
      return load()
    }
  } catch (e) {
    error.value = e.response?.status === 401 ? '未授权，请重新登录' : e.message
    rows.value = []
    totalElements.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

function formatTime(v) {
  if (!v) return '—'
  return new Date(v).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<style scoped>
.panel {
  padding: 0;
  overflow: hidden;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 12px 16px;
  padding: 20px 20px 16px;
  border-bottom: 1px solid var(--border);
  background: #fafbfc;
}

.toolbar-field {
  width: 160px;
}

.toolbar-search {
  flex: 1;
  min-width: 200px;
}

.toolbar-btn {
  height: var(--control-height);
}

.loading {
  display: flex;
  justify-content: center;
  padding: 56px;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

thead th {
  padding: 12px 16px;
  text-align: left;
  font-size: 12px;
  font-weight: 600;
  text-transform: none;
  letter-spacing: 0.02em;
  color: var(--text-secondary);
  background: #fafbfc;
  border-bottom: 1px solid var(--border);
  white-space: nowrap;
}

tbody td {
  padding: 14px 16px;
  border-bottom: 1px solid var(--border);
  vertical-align: top;
}

tbody tr {
  transition: background 0.12s ease;
}

tbody tr:hover {
  background: #f8fafc;
}

tbody tr:last-child td {
  border-bottom: none;
}

.mono {
  font-variant-numeric: tabular-nums;
  font-size: 13px;
  color: var(--text);
}

.desc {
  max-width: 280px;
  color: var(--text-secondary);
  line-height: 1.45;
}

.time {
  white-space: nowrap;
  font-size: 13px;
  color: var(--text-secondary);
}

.link-detail {
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
}

.link-detail:hover {
  text-decoration: underline;
}

.table-foot {
  padding: 12px 20px;
  font-size: 13px;
  color: var(--text-muted);
  border-top: 1px solid var(--border);
  background: #fafbfc;
}

.pager {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.pager-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pager-size {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
}

.pager-size .select {
  width: auto;
  padding: 6px 10px;
}

.pager-btn {
  padding: 6px 14px;
}

.pager-num {
  min-width: 72px;
  text-align: center;
  color: var(--text-secondary);
}
</style>
