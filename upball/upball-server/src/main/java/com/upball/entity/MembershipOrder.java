package com.upball.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("membership_orders")
public class MembershipOrder {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String orderNo;
    private Long teamId;
    private Long userId;
    private Long planId;
    private String planName;
    private Integer planType;
    private Integer planLevel;
    
    private BigDecimal amount;
    
    /**
     * 支付方式: 1-微信 2-支付宝 3-现金
     */
    private Integer payType;
    
    /**
     * 支付状态: 0-未支付 1-已支付 2-已退款
     */
    private Integer payStatus;
    
    private LocalDateTime paidAt;
    private String tradeNo;
    
    /**
     * 获得等级: 1-白银 2-黄金 3-VIP
     */
    private Integer level;
    
    private Integer totalTimes;
    private Integer usedTimes;
    private Integer remainingTimes;
    private LocalDate validStartDate;
    private LocalDate validEndDate;
    
    /**
     * 状态: 0-作废 1-有效 2-已用完 3-已过期
     */
    private Integer status;
    
    private String remark;
    private Long createdBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
