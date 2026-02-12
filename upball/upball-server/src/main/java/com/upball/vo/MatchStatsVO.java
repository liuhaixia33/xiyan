package com.upball.vo;

import lombok.Data;

@Data
public class MatchStatsVO {
    
    private Long matchId;
    
    // 报名统计
    private Integer totalInvited;
    private Integer confirmed;
    private Integer pending;
    private Integer declined;
    private Integer noResponse;
    
    // 比赛数据
    private Integer starterCount;
    private Integer substituteCount;
    private Integer totalGoals;
    private Integer totalAssists;
    private Integer totalYellowCards;
    private Integer totalRedCards;
    
    // 出勤率
    private Double attendanceRate;
}
