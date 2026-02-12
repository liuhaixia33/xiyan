import Taro from '@tarojs/taro';

// API 基础地址
const BASE_URL = 'http://localhost:8080/api';

interface RequestOptions {
  url: string;
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  data?: any;
  header?: any;
}

interface ResponseData<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

// 请求拦截
const request = <T = any>(options: RequestOptions): Promise<T> => {
  return new Promise((resolve, reject) => {
    const token = Taro.getStorageSync('token');
    
    Taro.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header,
      },
      success: (res) => {
        const data = res.data as ResponseData<T>;
        
        if (data.code === 200) {
          resolve(data.data);
        } else if (data.code === 401) {
          // Token 过期，清除登录状态
          Taro.removeStorageSync('token');
          Taro.removeStorageSync('userInfo');
          Taro.showToast({
            title: '登录已过期',
            icon: 'none',
          });
          reject(new Error(data.message));
        } else {
          Taro.showToast({
            title: data.message || '请求失败',
            icon: 'none',
          });
          reject(new Error(data.message));
        }
      },
      fail: (err) => {
        Taro.showToast({
          title: '网络错误',
          icon: 'none',
        });
        reject(err);
      },
    });
  });
};

// GET 请求
export const get = <T = any>(url: string, params?: any): Promise<T> => {
  return request<T>({
    url: params ? `${url}?${Object.keys(params).map(key => `${key}=${params[key]}`).join('&')}` : url,
    method: 'GET',
  });
};

// POST 请求
export const post = <T = any>(url: string, data?: any): Promise<T> => {
  return request<T>({
    url,
    method: 'POST',
    data,
  });
};

// PUT 请求
export const put = <T = any>(url: string, data?: any): Promise<T> => {
  return request<T>({
    url,
    method: 'PUT',
    data,
  });
};

// DELETE 请求
export const del = <T = any>(url: string): Promise<T> => {
  return request<T>({
    url,
    method: 'DELETE',
  });
};

export default request;
