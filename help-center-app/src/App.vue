<template>
  <MockLogin v-if="!user" @success="onLogin" />
  <HelpCenterChat v-else :user="user" @switch-user="onSwitchUser" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MockLogin from './views/MockLogin.vue'
import HelpCenterChat from './views/HelpCenterChat.vue'
import { loadUser, clearUser } from './utils/mockUser.js'

const user = ref(null)

onMounted(() => {
  user.value = loadUser()
})

function onLogin(u) {
  user.value = u
}

function onSwitchUser() {
  clearUser()
  user.value = null
}
</script>
