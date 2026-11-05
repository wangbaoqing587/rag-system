import { createRouter, createWebHistory } from 'vue-router'
import TicketList from './views/TicketList.vue'
import TicketDetail from './views/TicketDetail.vue'
import Login from './views/Login.vue'
import { TOKEN_KEY } from './api/request.js'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login, meta: { layout: false, public: true } },
    { path: '/', component: TicketList },
    { path: '/tickets/:id', component: TicketDetail, props: true },
  ],
})

router.beforeEach((to) => {
  if (to.meta.public) {
    return true
  }
  if (!localStorage.getItem(TOKEN_KEY)) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
