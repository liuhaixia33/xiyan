package com.upball.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("membership_plans")
public class MembershipPlan {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long teamId;
    private String name;
    
    /**
     * 类型: 1-次卡 2-周期卡
     */
    private Integer type;
    
    /**
     * 等级: 1-白银 2-黄金 3-VIP
     */
    private Integer level;
    
    private Integer times;
    private Integer durationMonths;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String description;
    private String benefits;
    
    /**
     * 状态: 0-下架 1-上架
     */
    private Integer status;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
