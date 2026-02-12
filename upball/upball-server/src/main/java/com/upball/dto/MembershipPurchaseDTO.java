package com.upball.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MembershipPurchaseDTO {
    
    @NotNull(message = "球队ID不能为空")
    private Long teamId;
    
    @NotNull(message = "套餐ID不能为空")
    private Long planId;
    
    /**
     * 支付方式: 1-微信 2-支付宝
     */
    private Integer payType = 1;
}
