package com.upball.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("matches")
public class Match {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String title;
    
    /**
     * 类型: 1-友谊赛 2-联赛 3-杯赛 4-锦标赛
     */
    private Integer type;
    
    private Long homeTeamId;
    private Long awayTeamId;
    private LocalDateTime matchTime;
    private String venue;
    
    /**
     * 状态: 0-未开始 1-进行中 2-已结束 3-取消
     */
    private Integer status;
    
    private Integer homeScore;
    private Integer awayScore;
    private String description;
    private String referee;
    
    /**
     * 费用类型: 0-免费 1-AA制 2-主队承担 3-客队承担
     */
    private Integer feeType;
    
    private BigDecimal feeAmount;
    private Long createdBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
