package com.upball.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String openid;
    private String unionid;
    private String nickname;
    private String avatar;
    private String phone;
    private String realName;
    private String idCard;
    
    /**
     * 状态: 0-禁用 1-正常
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
