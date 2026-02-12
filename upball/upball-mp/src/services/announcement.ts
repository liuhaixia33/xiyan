import { get, post, del } from '../utils/request';

// 获取球队公告列表
export const getTeamAnnouncements = (teamId: number, page: number = 1, size: number = 10) => {
  return get('/v1/announcements/team/' + teamId, { page, size });
};

// 获取最新公告
export const getLatestAnnouncements = (teamId: number, limit: number = 5) => {
  return get('/v1/announcements/team/' + teamId + '/latest', { limit });
};

// 获取公告详情
export const getAnnouncementDetail = (id: number) => {
  return get('/v1/announcements/' + id);
};

// 发布公告
export const createAnnouncement = (data: any) => {
  return post('/v1/announcements', data);
};

// 删除公告
export const deleteAnnouncement = (id: number) => {
  return del('/v1/announcements/' + id);
};

// 点赞/取消点赞
export const likeAnnouncement = (id: number) => {
  return post('/v1/announcements/' + id + '/like');
};

// 置顶/取消置顶
export const toggleTop = (id: number, isTop: number) => {
  return post('/v1/announcements/' + id + '/top', { isTop });
};
