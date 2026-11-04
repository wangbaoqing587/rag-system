<template>
  <div class="panel">
    <button type="button" class="toggle" @click="open = !open">
      分类咨询 {{ open ? '▲' : '▼' }}
    </button>
    <div v-if="open" class="cats">
      <button v-for="c in categories" :key="c.id" type="button" @click="$emit('pick', c.question)">
        {{ c.label }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineEmits(['pick'])

const open = ref(false)

const categories = [
  { id: 1, label: '账户与安全', question: '如何修改绑定手机？' },
  { id: 2, label: '充值与礼物币', question: '礼物币充值不到账怎么办？' },
  { id: 3, label: '退款与订单', question: '如何申请退款？' },
]
</script>

<style scoped>
.panel {
  margin-bottom: 8px;
}
.toggle {
  width: 100%;
  border: 1px solid #e8e8e8;
  background: #fff;
  border-radius: 10px;
  padding: 10px 12px;
  text-align: left;
  color: #333;
}
.cats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 8px;
}
.cats button {
  border: 1px solid #eef0f5;
  background: #fff;
  border-radius: 10px;
  padding: 10px 12px;
  text-align: left;
}
</style>
