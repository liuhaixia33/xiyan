package com.upball.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnnouncementVO {
    
    private Long id;
    private Long teamId;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    
    /**
     * 类型: 1-普通公告 2-重要通知 3-活动邀请
     */
    private Integer type;
    private String typeName;
    
    private String title;
    private String content;
    private List<String> imageList;
    
    private Integer isTop;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean hasLiked;
    
    private LocalDateTime createdAt;
    private String timeAgo; // 几分钟前/几小时前
}
