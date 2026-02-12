package com.upball.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("announcements")
public class Announcement {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long teamId;
    
    private Long authorId;
    
    /**
     * 公告类型: 1-普通公告 2-重要通知 3-活动邀请
     */
    private Integer type;
    
    private String title;
    
    private String content;
    
    /**
     * 图片URL，逗号分隔
     */
    private String images;
    
    /**
     * 置顶: 0-否 1-是
     */
    private Integer isTop;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
