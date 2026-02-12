package com.upball.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.upball.dto.AnnouncementCreateDTO;
import com.upball.entity.Announcement;
import com.upball.entity.User;
import com.upball.enums.ResultCode;
import com.upball.exception.BusinessException;
import com.upball.mapper.AnnouncementMapper;
import com.upball.mapper.UserMapper;
import com.upball.service.AnnouncementService;
import com.upball.service.TeamService;
import com.upball.utils.SnowflakeIdUtil;
import com.upball.vo.AnnouncementVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private SnowflakeIdUtil idUtil;
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Announcement createAnnouncement(Long userId, AnnouncementCreateDTO dto) {
        // 检查权限
        if (!teamService.hasManagePermission(dto.getTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        Announcement announcement = new Announcement();
        announcement.setId(idUtil.nextId());
        announcement.setTeamId(dto.getTeamId());
        announcement.setAuthorId(userId);
        announcement.setType(dto.getType());
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        
        // 处理图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            announcement.setImages(String.join(",", dto.getImages()));
        }
        
        announcement.setIsTop(dto.getIsTop());
        announcement.setViewCount(0);
        announcement.setLikeCount(0);
        announcement.setCommentCount(0);
        
        announcementMapper.insert(announcement);
        
        log.info("发布公告: id={}, teamId={}, title={}", announcement.getId(), dto.getTeamId(), dto.getTitle());
        return announcement;
    }

    @Override
    public List<AnnouncementVO> getTeamAnnouncements(Long teamId, int page, int size) {
        Page<Announcement> pageParam = new Page<>(page, size);
        Page<Announcement> result = announcementMapper.selectPage(pageParam, 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getTeamId, teamId)
                .eq(Announcement::getDeleted, 0)
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreatedAt));
        
        return result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnouncementVO> getLatestAnnouncements(Long teamId, int limit) {
        List<Announcement> list = announcementMapper.selectLatestByTeamId(teamId, limit);
        return list.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public AnnouncementVO getAnnouncementDetail(Long announcementId, Long userId) {
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null || announcement.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 增加浏览次数
        announcementMapper.incrementViewCount(announcementId);
        
        AnnouncementVO vo = convertToVO(announcement);
        
        // 检查是否已点赞
        String likeKey = "announcement:like:" + announcementId + ":" + userId;
        vo.setHasLiked(redisTemplate.hasKey(likeKey));
        
        return vo;
    }

    @Override
    public void deleteAnnouncement(Long announcementId, Long userId) {
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null || announcement.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 检查权限（只有作者或队长可以删除）
        if (!announcement.getAuthorId().equals(userId) && 
            !teamService.hasManagePermission(announcement.getTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        announcementMapper.deleteById(announcementId);
        log.info("删除公告: id={}", announcementId);
    }

    @Override
    public void likeAnnouncement(Long announcementId, Long userId) {
        String likeKey = "announcement:like:" + announcementId + ":" + userId;
        
        // 检查是否已经点赞
        if (redisTemplate.hasKey(likeKey)) {
            // 取消点赞
            redisTemplate.delete(likeKey);
            announcementMapper.update(null, 
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Announcement>()
                    .setSql("like_count = like_count - 1")
                    .eq(Announcement::getId, announcementId));
        } else {
            // 点赞
            redisTemplate.opsForValue().set(likeKey, "1", Duration.ofDays(30));
            announcementMapper.incrementLikeCount(announcementId);
        }
    }

    @Override
    public void toggleTop(Long announcementId, Long userId, Integer isTop) {
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null || announcement.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (!teamService.hasManagePermission(announcement.getTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        announcement.setIsTop(isTop);
        announcementMapper.updateById(announcement);
        
        log.info("{}公告: id={}", isTop == 1 ? "置顶" : "取消置顶", announcementId);
    }
    
    private AnnouncementVO convertToVO(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        BeanUtils.copyProperties(announcement, vo);
        
        // 获取作者信息
        User author = userMapper.selectById(announcement.getAuthorId());
        if (author != null) {
            vo.setAuthorName(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
        }
        
        // 处理图片列表
        if (announcement.getImages() != null && !announcement.getImages().isEmpty()) {
            vo.setImageList(Arrays.asList(announcement.getImages().split(",")));
        }
        
        // 类型名称
        String[] typeNames = {"", "普通公告", "重要通知", "活动邀请"};
        if (announcement.getType() >= 1 && announcement.getType() <= 3) {
            vo.setTypeName(typeNames[announcement.getType()]);
        }
        
        // 计算时间差
        vo.setTimeAgo(calculateTimeAgo(announcement.getCreatedAt()));
        
        return vo;
    }
    
    private String calculateTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        
        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 30) {
            return days + "天前";
        } else {
            return createdAt.getMonthValue() + "月" + createdAt.getDayOfMonth() + "日";
        }
    }
}
