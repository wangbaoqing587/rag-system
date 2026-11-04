<template>
  <div v-if="sources.length" class="sources">
    <div class="label">参考来源</div>
    <div v-for="(s, i) in visible" :key="i" class="item">
      <div class="title">{{ s.title }}</div>
      <div class="snippet">{{ s.snippet }}</div>
    </div>
    <button v-if="sources.length > max" type="button" class="more" @click="expanded = !expanded">
      {{ expanded ? '收起' : `展开全部 (${sources.length})` }}
    </button>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  sources: { type: Array, default: () => [] },
  max: { type: Number, default: 2 },
})

const expanded = ref(false)

const visible = computed(() =>
  expanded.value ? props.sources : props.sources.slice(0, props.max)
)
</script>

<style scoped>
.sources {
  margin-top: 8px;
  padding: 10px 12px;
  background: #eef0f4;
  border-radius: 10px;
  font-size: 12px;
}
.label {
  color: #666;
  font-weight: 600;
  margin-bottom: 8px;
}
.item {
  margin-bottom: 8px;
}
.title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}
.snippet {
  color: #666;
  line-height: 1.4;
}
.more {
  border: none;
  background: none;
  color: var(--color-primary);
  padding: 0;
  font-size: 12px;
}
</style>
