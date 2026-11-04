<template>
  <span :class="['tag', status]">{{ label }}</span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: String,
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
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  background: #eee;
}
.PENDING {
  background: #fff7e6;
  color: #d48806;
}
.IN_PROGRESS {
  background: #e6f4ff;
  color: #1677ff;
}
.RESOLVED {
  background: #f6ffed;
  color: #389e0d;
}
.CLOSED {
  background: #f5f5f5;
  color: #666;
}
</style>
