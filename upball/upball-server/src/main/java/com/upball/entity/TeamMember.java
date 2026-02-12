package com.upball.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("team_members")
public class TeamMember {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long teamId;
    private Long userId;
    
    /**
     * 角色: 1-队长 2-副队长 3-队员 4-候补
     */
    private Integer role;
    
    private String position;
    private Integer jerseyNumber;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinAt;
    
    /**
     * 状态: 0-退出 1-正常 2-请假中
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
