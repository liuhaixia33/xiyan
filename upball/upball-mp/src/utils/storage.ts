import Taro from '@tarojs/taro';

// 存储 Token
export const setToken = (token: string) => {
  Taro.setStorageSync('token', token);
};

// 获取 Token
export const getToken = (): string => {
  return Taro.getStorageSync('token');
};

// 清除 Token
export const removeToken = () => {
  Taro.removeStorageSync('token');
};

// 存储用户信息
export const setUserInfo = (userInfo: any) => {
  Taro.setStorageSync('userInfo', userInfo);
};

// 获取用户信息
export const getUserInfo = (): any => {
  return Taro.getStorageSync('userInfo');
};

// 存储当前球队
export const setCurrentTeam = (team: any) => {
  Taro.setStorageSync('currentTeam', team);
};

// 获取当前球队
export const getCurrentTeam = (): any => {
  return Taro.getStorageSync('currentTeam');
};
