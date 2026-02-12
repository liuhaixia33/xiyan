package com.upball.service;

import com.upball.dto.AnnouncementCreateDTO;
import com.upball.entity.Announcement;
import com.upball.vo.AnnouncementVO;

import java.util.List;

public interface AnnouncementService {
    
    /**
     * 发布公告
     */
    Announcement createAnnouncement(Long userId, AnnouncementCreateDTO dto);
    
    /**
     * 获取球队公告列表
     */
    List<AnnouncementVO> getTeamAnnouncements(Long teamId, int page, int size);
    
    /**
     * 获取最新公告（用于预览）
     */
    List<AnnouncementVO> getLatestAnnouncements(Long teamId, int limit);
    
    /**
     * 获取公告详情
     */
    AnnouncementVO getAnnouncementDetail(Long announcementId, Long userId);
    
    /**
     * 删除公告
     */
    void deleteAnnouncement(Long announcementId, Long userId);
    
    /**
     * 点赞公告
     */
    void likeAnnouncement(Long announcementId, Long userId);
    
    /**
     * 置顶/取消置顶
     */
    void toggleTop(Long announcementId, Long userId, Integer isTop);
}
