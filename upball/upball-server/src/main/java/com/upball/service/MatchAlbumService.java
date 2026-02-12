package com.upball.service;

import com.upball.dto.AlbumUploadDTO;
import com.upball.vo.MatchAlbumVO;

import java.util.List;

public interface MatchAlbumService {
    
    /**
     * 上传照片
     */
    MatchAlbumVO uploadPhoto(Long userId, AlbumUploadDTO dto);
    
    /**
     * 批量上传
     */
    List<MatchAlbumVO> batchUpload(Long userId, List<AlbumUploadDTO> dtoList);
    
    /**
     * 获取赛事相册
     */
    List<MatchAlbumVO> getMatchAlbums(Long matchId, Long currentUserId);
    
    /**
     * 获取相册预览（最近几张）
     */
    List<MatchAlbumVO> getAlbumPreview(Long matchId, int limit);
    
    /**
     * 删除照片
     */
    void deletePhoto(Long albumId, Long userId);
    
    /**
     * 设为封面
     */
    void setAsCover(Long albumId, Long userId);
    
    /**
     * 点赞/取消点赞
     */
    void likePhoto(Long albumId, Long userId);
    
    /**
     * 更新描述
     */
    void updateDescription(Long albumId, Long userId, String description);
    
    /**
     * 获取照片数量
     */
    int getPhotoCount(Long matchId);
}
