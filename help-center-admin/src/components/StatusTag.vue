<template>
  <span :class="['tag', status, variant]">
    <span class="dot" aria-hidden="true" />
    {{ label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: String,
  variant: {
    type: String,
    default: 'default',
  },
})

const map = {
  PENDING: '待处理',
  IN_PROGRESS: '处理中',
  RESOLVED: '已解决',
  CLOSED: '已关闭',
}

const label = computed(() => map[props.status] || props.status)
</script>

<style scoped>
.tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
  vertical-align: middle;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.tag.default {
  height: 20px;
  box-sizing: border-box;
  padding: 0 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  line-height: 1;
  gap: 4px;
  border: 1px solid transparent;
}

.default .dot {
  width: 5px;
  height: 5px;
}

.default.PENDING {
  background: #fff7ed;
  color: #c2410c;
  border-color: #fed7aa;
}

.default.PENDING .dot {
  background: #ea580c;
}

.default.IN_PROGRESS {
  background: #eff6ff;
  color: #1d4ed8;
  border-color: #bfdbfe;
}

.default.IN_PROGRESS .dot {
  background: #2563eb;
}

.default.RESOLVED {
  background: #f0fdf4;
  color: #15803d;
  border-color: #bbf7d0;
}

.default.RESOLVED .dot {
  background: #16a34a;
}

.default.CLOSED {
  background: #f8fafc;
  color: #475569;
  border-color: #e2e8f0;
}

.default.CLOSED .dot {
  background: #64748b;
}

.tag.hero {
  padding: 4px 12px 4px 10px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.01em;
  border: 1px solid;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.06);
}

.hero .dot {
  width: 7px;
  height: 7px;
}

.hero.PENDING {
  background: #fff7ed;
  color: #9a3412;
  border-color: #fdba74;
}

.hero.PENDING .dot {
  background: #ea580c;
  box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.18);
}

.hero.IN_PROGRESS {
  background: #eff6ff;
  color: #1e40af;
  border-color: #93c5fd;
}

.hero.IN_PROGRESS .dot {
  background: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.18);
}

.hero.RESOLVED {
  background: #ecfdf5;
  color: #166534;
  border-color: #86efac;
}

.hero.RESOLVED .dot {
  background: #16a34a;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.18);
}

.hero.CLOSED {
  background: #f1f5f9;
  color: #334155;
  border-color: #cbd5e1;
}

.hero.CLOSED .dot {
  background: #64748b;
  box-shadow: 0 0 0 3px rgba(100, 116, 139, 0.15);
}
</style>
