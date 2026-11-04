<template>
  <div class="chips">
    <button v-for="q in questions" :key="q" type="button" @click="$emit('pick', q)">{{ q }}</button>
  </div>
</template>

<script setup>
defineEmits(['pick'])

const questions = [
  '礼物币充值不到账怎么办？',
  '如何申请退款？',
  '直播卡顿怎么排查？',
]
</script>

<style scoped>
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}
.chips button {
  border: 1px solid var(--color-primary-soft-border);
  background: #fff;
  color: var(--color-primary);
  border-radius: 999px;
  padding: 8px 12px;
  font-size: 13px;
}
</style>
