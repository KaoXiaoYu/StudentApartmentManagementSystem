import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'home' }
      },
      // 学生管理
      {
        path: 'students',
        name: 'Students',
        component: () => import('@/views/students/index.vue'),
        meta: { title: '学生管理', icon: 'user' }
      },
      // 寝室信息管理
      {
        path: 'dormitories',
        name: 'Dormitories',
        component: () => import('@/views/dormitories/index.vue'),
        meta: { title: '寝室信息', icon: 'building' }
      },
      // 寝室安排管理
      {
        path: 'allocation',
        name: 'Allocation',
        component: () => import('@/views/allocation/index.vue'),
        meta: { title: '寝室安排', icon: 'layout' }
      },
      // 卫生检查管理
      {
        path: 'sanitation',
        name: 'Sanitation',
        component: () => import('@/views/sanitation/index.vue'),
        meta: { title: '卫生检查', icon: 'check' }
      },
      // 文明寝室管理
      {
        path: 'civilized',
        name: 'Civilized',
        component: () => import('@/views/civilized/index.vue'),
        meta: { title: '文明寝室', icon: 'award' }
      },
      // 缴费信息管理
      {
        path: 'payment',
        name: 'Payment',
        component: () => import('@/views/payment/index.vue'),
        meta: { title: '缴费管理', icon: 'wallet' }
      },
      // 权限授权管理
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/permission/index.vue'),
        meta: { title: '权限管理', icon: 'lock' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
