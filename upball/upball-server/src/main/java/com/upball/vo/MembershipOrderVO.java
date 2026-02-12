package com.upball.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MembershipOrderVO {
    
    private Long orderId;
    private String planName;
    private Integer planType;
    private Integer level;
    private Integer totalTimes;
    private Integer usedTimes;
    private Integer remainingTimes;
    private LocalDate validStartDate;
    private LocalDate validEndDate;
    private Integer status;
    private LocalDateTime createdAt;
}
