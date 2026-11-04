<template>
  <div class="avatar">
    <img :src="iconSrc" alt="" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import icAssistant from '../assets/ic_assistant.svg'
import icUser from '../assets/ic_user.svg'

const props = defineProps({
  role: {
    type: String,
    required: true,
    validator: (v) => v === 'assistant' || v === 'user',
  },
})

const iconSrc = computed(() => (props.role === 'user' ? icUser : icAssistant))
</script>

<style scoped>
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e4e6eb;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}
.avatar img {
  width: 22px;
  height: 22px;
  display: block;
  object-fit: contain;
}
</style>
