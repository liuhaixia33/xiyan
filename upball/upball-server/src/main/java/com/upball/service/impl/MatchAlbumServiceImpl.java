package com.upball.service.impl;

import com.upball.dto.AlbumUploadDTO;
import com.upball.entity.Match;
import com.upball.entity.MatchAlbum;
import com.upball.entity.User;
import com.upball.enums.ResultCode;
import com.upball.exception.BusinessException;
import com.upball.mapper.MatchAlbumMapper;
import com.upball.mapper.MatchMapper;
import com.upball.mapper.UserMapper;
import com.upball.service.MatchAlbumService;
import com.upball.service.TeamService;
import com.upball.utils.SnowflakeIdUtil;
import com.upball.vo.MatchAlbumVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchAlbumServiceImpl implements MatchAlbumService {

    @Autowired
    private MatchAlbumMapper albumMapper;
    
    @Autowired
    private MatchMapper matchMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private SnowflakeIdUtil idUtil;
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchAlbumVO uploadPhoto(Long userId, AlbumUploadDTO dto) {
        // 检查赛事是否存在
        Match match = matchMapper.selectById(dto.getMatchId());
        if (match == null || match.getDeleted() == 1) {
            throw new BusinessException(ResultCode.MATCH_NOT_FOUND);
        }
        
        // 检查是否是球队成员
        if (!teamService.isTeamMember(dto.getTeamId(), userId)) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        MatchAlbum album = new MatchAlbum();
        album.setId(idUtil.nextId());
        album.setMatchId(dto.getMatchId());
        album.setTeamId(dto.getTeamId());
        album.setUserId(userId);
        album.setImageUrl(dto.getImageUrl());
        album.setThumbnailUrl(dto.getThumbnailUrl());
        album.setDescription(dto.getDescription());
        album.setFileSize(dto.getFileSize());
        album.setWidth(dto.getWidth());
        album.setHeight(dto.getHeight());
        
        // 如果是第一张设为封面，或者用户指定设为封面
        int count = albumMapper.countByMatchId(dto.getMatchId());
        if (count == 0 || dto.getIsCover() == 1) {
            if (dto.getIsCover() == 1) {
                // 清除原有封面
                albumMapper.clearCover(dto.getMatchId());
            }
            album.setIsCover(1);
        } else {
            album.setIsCover(0);
        }
        
        album.setLikeCount(0);
        albumMapper.insert(album);
        
        log.info("上传赛事照片: albumId={}, matchId={}, userId={}", album.getId(), dto.getMatchId(), userId);
        return convertToVO(album, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MatchAlbumVO> batchUpload(Long userId, List<AlbumUploadDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        
        return dtoList.stream()
                .map(dto -> uploadPhoto(userId, dto))
                .collect(Collectors.toList());
    }

    @Override
    public List<MatchAlbumVO> getMatchAlbums(Long matchId, Long currentUserId) {
        List<MatchAlbum> list = albumMapper.selectByMatchId(matchId);
        return list.stream()
                .map(album -> convertToVO(album, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MatchAlbumVO> getAlbumPreview(Long matchId, int limit) {
        List<MatchAlbum> list = albumMapper.selectByMatchIdLimit(matchId, limit);
        return list.stream()
                .map(album -> convertToVO(album, null))
                .collect(Collectors.toList());
    }

    @Override
    public void deletePhoto(Long albumId, Long userId) {
        MatchAlbum album = albumMapper.selectById(albumId);
        if (album == null || album.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 检查权限：上传者或队长可删除
        if (!album.getUserId().equals(userId) && 
            !teamService.hasManagePermission(album.getTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        albumMapper.deleteById(albumId);
        
        // 如果删除的是封面，自动设置最新一张为封面
        if (album.getIsCover() == 1) {
            List<MatchAlbum> remaining = albumMapper.selectByMatchIdLimit(album.getMatchId(), 1);
            if (!remaining.isEmpty()) {
                albumMapper.setAsCover(remaining.get(0).getId());
            }
        }
        
        log.info("删除赛事照片: albumId={}", albumId);
    }

    @Override
    public void setAsCover(Long albumId, Long userId) {
        MatchAlbum album = albumMapper.selectById(albumId);
        if (album == null || album.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 只有队长/副队长可以设置封面
        if (!teamService.hasManagePermission(album.getTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        // 清除原有封面
        albumMapper.clearCover(album.getMatchId());
        // 设置新封面
        albumMapper.setAsCover(albumId);
        
        log.info("设置封面: albumId={}", albumId);
    }

    @Override
    public void likePhoto(Long albumId, Long userId) {
        String likeKey = "album:like:" + albumId + ":" + userId;
        
        if (redisTemplate.hasKey(likeKey)) {
            // 取消点赞
            redisTemplate.delete(likeKey);
            albumMapper.decrementLikeCount(albumId);
        } else {
            // 点赞
            redisTemplate.opsForValue().set(likeKey, "1", Duration.ofDays(30));
            albumMapper.incrementLikeCount(albumId);
        }
    }

    @Override
    public void updateDescription(Long albumId, Long userId, String description) {
        MatchAlbum album = albumMapper.selectById(albumId);
        if (album == null || album.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 只有上传者或队长可以修改
        if (!album.getUserId().equals(userId) && 
            !teamService.hasManagePermission(album.getTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        album.setDescription(description);
        albumMapper.updateById(album);
    }

    @Override
    public int getPhotoCount(Long matchId) {
        return albumMapper.countByMatchId(matchId);
    }
    
    private MatchAlbumVO convertToVO(MatchAlbum album, Long currentUserId) {
        MatchAlbumVO vo = new MatchAlbumVO();
        BeanUtils.copyProperties(album, vo);
        
        // 获取上传者信息
        User user = userMapper.selectById(album.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }
        
        // 检查是否已点赞
        if (currentUserId != null) {
            String likeKey = "album:like:" + album.getId() + ":" + currentUserId;
            vo.setHasLiked(redisTemplate.hasKey(likeKey));
        }
        
        // 计算时间差
        vo.setTimeAgo(calculateTimeAgo(album.getCreatedAt()));
        
        return vo;
    }
    
    private String calculateTimeAgo(LocalDateTime createdAt) {
        java.time.Duration duration = java.time.Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        
        if (minutes < 1) return "刚刚";
        if (minutes < 60) return minutes + "分钟前";
        if (hours < 24) return hours + "小时前";
        if (days < 30) return days + "天前";
        return createdAt.getMonthValue() + "月" + createdAt.getDayOfMonth() + "日";
    }
}
