// 运行时配置
import { RequestConfig } from '@umijs/max';

// 请求配置
export const request: RequestConfig = {
  timeout: 10000,
  errorConfig: {
    errorHandler() {},
    errorThrower() {},
  },
  requestInterceptors: [
    (config: any) => {
      // 添加 token
      const token = localStorage.getItem('admin_token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
  ],
  responseInterceptors: [
    (response: any) => {
      const { data } = response;
      if (data.code !== 200) {
        throw new Error(data.message || '请求失败');
      }
      return response;
    },
  ],
};

// 初始状态
export async function getInitialState(): Promise<any> {
  const token = localStorage.getItem('admin_token');
  if (token) {
    return {
      isLogin: true,
      userInfo: JSON.parse(localStorage.getItem('admin_user') || '{}'),
    };
  }
  return {
    isLogin: false,
  };
}
