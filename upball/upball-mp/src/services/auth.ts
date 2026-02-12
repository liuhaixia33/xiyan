import { post } from '../utils/request';

// 微信登录
export const wxLogin = (code: string, userInfo?: any) => {
  return post('/v1/auth/wx-login', {
    code,
    nickname: userInfo?.nickName,
    avatar: userInfo?.avatarUrl,
  });
};
