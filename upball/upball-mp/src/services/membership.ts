import { get, post } from '../utils/request';

// 获取会员套餐列表
export const getMembershipPlans = (teamId: number) => {
  return get('/v1/membership/plans', { teamId });
};

// 购买会员
export const purchaseMembership = (data: any) => {
  return post('/v1/membership/purchase', data);
};

// 获取我的会员状态
export const getMyMembershipStatus = (teamId: number) => {
  return get('/v1/membership/my-status', { teamId });
};

// 获取我的购买记录
export const getMyOrders = (teamId: number) => {
  return get('/v1/membership/my-orders', { teamId });
};

// 检查是否可以报名
export const checkCanRegister = (teamId: number) => {
  return get('/v1/membership/check-register', { teamId });
};
