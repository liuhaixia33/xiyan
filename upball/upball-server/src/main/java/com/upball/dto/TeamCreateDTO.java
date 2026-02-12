package com.upball.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeamCreateDTO {
    
    @NotBlank(message = "球队名称不能为空")
    @Size(max = 100, message = "球队名称最多100字符")
    private String name;
    
    private String logo;
    
    @Size(max = 500, message = "简介最多500字符")
    private String description;
    
    private String city;
    
    private String homeGround;
    
    /**
     * 加入方式: 1-申请 2-邀请 3-公开
     */
    private Integer joinType = 1;
}
