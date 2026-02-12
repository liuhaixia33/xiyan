import { get, post, put } from '../utils/request';

// 获取我的球队列表
export const getMyTeams = () => {
  return get('/v1/teams/my');
};

// 获取球队详情
export const getTeamDetail = (teamId: number) => {
  return get(`/v1/teams/${teamId}`);
};

// 创建球队
export const createTeam = (data: any) => {
  return post('/v1/teams', data);
};

// 更新球队
export const updateTeam = (teamId: number, data: any) => {
  return put(`/v1/teams/${teamId}`, data);
};

// 加入球队
export const joinTeam = (teamId: number) => {
  return post(`/v1/teams/${teamId}/join`);
};

// 退出球队
export const quitTeam = (teamId: number) => {
  return post(`/v1/teams/${teamId}/quit`);
};

// 获取球队成员
export const getTeamMembers = (teamId: number) => {
  return get(`/v1/teams/${teamId}/members`);
};
