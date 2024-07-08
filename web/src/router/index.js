import { createRouter, createWebHistory } from 'vue-router'
import ZhanJi from '../views/ZhanJi.vue'
const routes = [
  {
    path: '/ZhanJi',
    name: 'ZhanJi',
    component: ZhanJi
  },
  {
    path: '/gongju',
    name: 'gongju',
    component: () => import(/* webpackChunkName: "gongju" */ '../views/ToolsViedw.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
