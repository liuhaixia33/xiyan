package com.upball.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AnnouncementCreateDTO {
    
    @NotNull(message = "球队ID不能为空")
    private Long teamId;
    
    /**
     * 类型: 1-普通公告 2-重要通知 3-活动邀请
     */
    private Integer type = 1;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    /**
     * 图片列表
     */
    private List<String> images;
    
    /**
     * 是否置顶: 0-否 1-是
     */
    private Integer isTop = 0;
}
