<template>
  <router-view v-slot="{ Component, route }">
    <AdminLayout v-if="route.meta.layout !== false">
      <component :is="Component" />
    </AdminLayout>
    <component v-else :is="Component" />
  </router-view>
</template>

<script setup>
import AdminLayout from './components/AdminLayout.vue'
</script>
