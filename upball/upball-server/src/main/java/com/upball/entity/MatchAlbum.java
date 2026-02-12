package com.upball.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("match_albums")
public class MatchAlbum {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long matchId;
    
    private Long teamId;
    
    private Long userId;
    
    /**
     * 原图URL
     */
    private String imageUrl;
    
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    
    /**
     * 图片描述
     */
    private String description;
    
    /**
     * 文件大小（字节）
     */
    private Integer fileSize;
    
    /**
     * 图片宽度
     */
    private Integer width;
    
    /**
     * 图片高度
     */
    private Integer height;
    
    /**
     * 是否封面：0-否 1-是
     */
    private Integer isCover;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
