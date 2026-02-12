import { get, post, put } from '../utils/request';

// 获取球队赛事列表
export const getTeamMatches = (teamId: number) => {
  return get(`/v1/matches/team/${teamId}`);
};

// 获取赛事详情
export const getMatchDetail = (matchId: number) => {
  return get(`/v1/matches/${matchId}`);
};

// 创建赛事
export const createMatch = (data: any) => {
  return post('/v1/matches', data);
};

// 更新赛事
export const updateMatch = (matchId: number, data: any) => {
  return put(`/v1/matches/${matchId}`, data);
};

// 取消赛事
export const cancelMatch = (matchId: number) => {
  return post(`/v1/matches/${matchId}/cancel`);
};

// 报名参赛
export const registerMatch = (matchId: number, teamId: number, data: any) => {
  return post(`/v1/matches/${matchId}/register?teamId=${teamId}`, data);
};

// 取消报名
export const cancelRegistration = (matchId: number) => {
  return post(`/v1/matches/${matchId}/unregister`);
};

// 录入比赛结果
export const recordResult = (matchId: number, data: any) => {
  return post(`/v1/matches/${matchId}/result`, data);
};

// 获取赛事统计
export const getMatchStats = (matchId: number) => {
  return get(`/v1/matches/${matchId}/stats`);
};

// 获取近期赛事
export const getRecentMatches = (teamId: number, limit: number = 5) => {
  return get(`/v1/matches/team/${teamId}/recent?limit=${limit}`);
};
