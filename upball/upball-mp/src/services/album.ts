import { get, post, del, put } from '../utils/request';

// 上传照片
export const uploadPhoto = (data: any) => {
  return post('/v1/match-albums/upload', data);
};

// 批量上传
export const batchUpload = (dataList: any[]) => {
  return post('/v1/match-albums/batch-upload', dataList);
};

// 获取赛事相册
export const getMatchAlbums = (matchId: number) => {
  return get('/v1/match-albums/match/' + matchId);
};

// 获取相册预览
export const getAlbumPreview = (matchId: number, limit: number = 4) => {
  return get('/v1/match-albums/match/' + matchId + '/preview', { limit });
};

// 获取照片数量
export const getPhotoCount = (matchId: number) => {
  return get('/v1/match-albums/match/' + matchId + '/count');
};

// 删除照片
export const deletePhoto = (albumId: number) => {
  return del('/v1/match-albums/' + albumId);
};

// 设为封面
export const setAsCover = (albumId: number) => {
  return post('/v1/match-albums/' + albumId + '/cover');
};

// 点赞/取消点赞
export const likePhoto = (albumId: number) => {
  return post('/v1/match-albums/' + albumId + '/like');
};

// 更新描述
export const updateDescription = (albumId: number, description: string) => {
  return put('/v1/match-albums/' + albumId + '/description', { description });
};
