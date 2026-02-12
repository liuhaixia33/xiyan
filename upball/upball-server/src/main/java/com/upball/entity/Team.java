package com.upball.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("teams")
public class Team {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String name;
    private String logo;
    private String description;
    private String city;
    private String homeGround;
    private LocalDate foundedAt;
    private Long captainId;
    private Long viceCaptainId;
    
    /**
     * 状态: 0-解散 1-正常 2-暂停
     */
    private Integer status;
    
    /**
     * 加入方式: 1-申请 2-邀请 3-公开
     */
    private Integer joinType;
    
    private Integer memberCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
