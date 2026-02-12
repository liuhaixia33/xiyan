import { defineConfig } from '@umijs/max';

export default defineConfig({
  antd: {},
  access: {},
  model: {},
  initialState: {},
  request: {},
  layout: {
    title: '涨球管理后台',
    locale: false,
  },
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      name: '数据概览',
      path: '/dashboard',
      component: './Dashboard',
      icon: 'DashboardOutlined',
    },
    {
      name: '球队管理',
      path: '/team',
      icon: 'TeamOutlined',
      routes: [
        {
          name: '球队列表',
          path: '/team/list',
          component: './Team/List',
        },
        {
          name: '球队审核',
          path: '/team/audit',
          component: './Team/Audit',
        },
      ],
    },
    {
      name: '用户管理',
      path: '/user',
      icon: 'UserOutlined',
      routes: [
        {
          name: '用户列表',
          path: '/user/list',
          component: './User/List',
        },
      ],
    },
    {
      name: '会员管理',
      path: '/membership',
      icon: 'CrownOutlined',
      routes: [
        {
          name: '套餐配置',
          path: '/membership/plan',
          component: './Membership/Plan',
        },
        {
          name: '订单管理',
          path: '/membership/order',
          component: './Membership/Order',
        },
      ],
    },
    {
      name: '财务管理',
      path: '/finance',
      icon: 'DollarOutlined',
      routes: [
        {
          name: '收支记录',
          path: '/finance/record',
          component: './Finance/Record',
        },
        {
          name: '财务报表',
          path: '/finance/report',
          component: './Finance/Report',
        },
      ],
    },
    {
      name: '系统设置',
      path: '/system',
      icon: 'SettingOutlined',
      routes: [
        {
          name: '权限管理',
          path: '/system/permission',
          component: './System/Permission',
        },
      ],
    },
  ],
  npmClient: 'npm',
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
});
