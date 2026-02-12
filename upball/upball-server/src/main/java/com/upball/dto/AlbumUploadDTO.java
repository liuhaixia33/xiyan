package com.upball.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlbumUploadDTO {
    
    @NotNull(message = "赛事ID不能为空")
    private Long matchId;
    
    @NotNull(message = "球队ID不能为空")
    private Long teamId;
    
    @NotBlank(message = "图片URL不能为空")
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
     * 文件大小
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
     * 是否设为封面
     */
    private Integer isCover = 0;
}
