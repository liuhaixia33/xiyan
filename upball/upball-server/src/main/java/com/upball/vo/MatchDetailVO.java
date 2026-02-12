package com.upball.vo;

import com.upball.entity.Match;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MatchDetailVO extends Match {
    
    private String homeTeamName;
    private String homeTeamLogo;
    private String awayTeamName;
    private String awayTeamLogo;
    
    /**
     * 报名人数统计
     */
    private Integer confirmedCount;
    private Integer pendingCount;
    private Integer declinedCount;
    
    /**
     * 当前用户报名状态
     */
    private Integer myStatus;
    
    /**
     * 参赛人员名单
     */
    private List<MatchAttendanceVO> attendanceList;
}
