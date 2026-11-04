import { createRouter, createWebHistory } from 'vue-router'
import TicketList from './views/TicketList.vue'
import TicketDetail from './views/TicketDetail.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: TicketList },
    { path: '/tickets/:id', component: TicketDetail, props: true },
  ],
})
