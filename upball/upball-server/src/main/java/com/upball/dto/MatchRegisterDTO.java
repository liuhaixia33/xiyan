package com.upball.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatchRegisterDTO {
    
    /**
     * 状态: 1-能参加 2-待定 3-不能参加
     */
    @NotNull(message = "报名状态不能为空")
    private Integer status;
    
    private String comment;
}
