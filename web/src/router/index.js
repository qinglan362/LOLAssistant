import { createRouter, createWebHistory } from 'vue-router'
import ZhanJi from '../views/ZhanJi.vue'
import TftView from "@/views/TftView.vue";
const routes = [
  {
    path: '/ZhanJi',
    name: 'ZhanJi',
    component: ZhanJi
  },
  {
    path: '/someoneTft',
    name: 'someoneTft',
    component: TftView
  },
  {
    path: '/gongju',
    name: 'gongju',
    component: () => import(/* webpackChunkName: "gongju" */ '../views/ToolsViedw.vue')
  },
  {
    path: '/current',
    name: 'current',
    component: () => import(/* webpackChunkName: "current" */ '../views/CurrentView.vue')
  },
  {
    path: '/someone',
    name: 'someone',
    component: () => import(/* webpackChunkName: "someone" */ '../views/SomeoneView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
})

export default router
