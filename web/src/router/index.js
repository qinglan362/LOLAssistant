import { createRouter, createWebHistory } from 'vue-router'
import ZhanJi from '../views/ZhanJi.vue'
const routes = [
  {
    path: '/ZhanJi',
    name: 'ZhanJi',
    component: ZhanJi
  },

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
