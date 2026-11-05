<template>
  <div class="chips">
    <button v-for="q in questions" :key="q" type="button" @click="$emit('pick', q)">{{ q }}</button>
  </div>
</template>

<script setup>
defineEmits(['pick'])

const questions = [
  '如何修改绑定手机号？',
  '充值不到账该怎么办？',
  '为什么进不了语音房？',
  '礼物送出失败怎么办？',
  '房间声音卡顿怎么办？',
  '会员没生效是怎么回事？',
  '怎么举报违规用户？',
  '如何联系人工客服？',
]
</script>

<style scoped>
.chips {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  margin-bottom: 8px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}
.chips::-webkit-scrollbar {
  display: none;
}
.chips button {
  border: 1px solid var(--color-primary-soft-border);
  background: #fff;
  color: var(--color-primary);
  border-radius: 999px;
  padding: 8px 12px;
  font-size: 13px;
  flex-shrink: 0;
  white-space: nowrap;
}
</style>
