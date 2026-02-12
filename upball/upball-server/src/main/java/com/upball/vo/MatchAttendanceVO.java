package com.upball.vo;

import lombok.Data;

@Data
public class MatchAttendanceVO {
    
    private Long userId;
    private String nickname;
    private String avatar;
    private Integer jerseyNumber;
    private String position;
    
    /**
     * 报名状态: 1-能参加 2-待定 3-不能参加
     */
    private Integer status;
    
    /**
     * 是否首发: 0-替补 1-首发
     */
    private Integer isStarter;
    
    private Integer goals;
    private Integer assists;
    private Double rating;
    private String comment;
}
