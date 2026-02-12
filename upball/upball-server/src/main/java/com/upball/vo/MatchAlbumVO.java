package com.upball.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchAlbumVO {
    
    private Long id;
    private Long matchId;
    private Long teamId;
    private Long userId;
    private String userName;
    private String userAvatar;
    
    private String imageUrl;
    private String thumbnailUrl;
    private String description;
    
    /**
     * 图片尺寸信息
     */
    private Integer width;
    private Integer height;
    
    /**
     * 是否封面
     */
    private Integer isCover;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean hasLiked;
    
    private LocalDateTime createdAt;
    private String timeAgo;
}
