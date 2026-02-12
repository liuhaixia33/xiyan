package com.upball.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MembershipStatusVO {
    
    private Long teamId;
    private Long userId;
    
    /**
     * 等级: 0-平民 1-白银 2-黄金 3-VIP
     */
    private Integer level;
    
    private String levelName;
    
    /**
     * 是否有效
     */
    private Boolean valid;
    
    /**
     * 次卡剩余次数
     */
    private Integer remainingTimes;
    
    /**
     * VIP到期日
     */
    private LocalDate expireDate;
    
    /**
     * 权益列表
     */
    private List<String> benefits;
    
    /**
     * 活跃订单列表
     */
    private List<MembershipOrderVO> activeOrders;
}
