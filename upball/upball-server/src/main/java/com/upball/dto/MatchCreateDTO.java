package com.upball.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MatchCreateDTO {
    
    @NotBlank(message = "赛事名称不能为空")
    private String title;
    
    /**
     * 类型: 1-友谊赛 2-联赛 3-杯赛 4-锦标赛
     */
    private Integer type = 1;
    
    @NotNull(message = "主队ID不能为空")
    private Long homeTeamId;
    
    private Long awayTeamId;
    
    @NotNull(message = "比赛时间不能为空")
    private LocalDateTime matchTime;
    
    private String venue;
    private String description;
    private String referee;
    
    /**
     * 费用类型: 0-免费 1-AA制 2-主队承担 3-客队承担
     */
    private Integer feeType = 0;
    
    private BigDecimal feeAmount;
}
