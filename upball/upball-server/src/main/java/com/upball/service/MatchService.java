package com.upball.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.upball.dto.MatchCreateDTO;
import com.upball.dto.MatchRegisterDTO;
import com.upball.dto.MatchResultDTO;
import com.upball.entity.Match;
import com.upball.vo.MatchDetailVO;
import com.upball.vo.MatchStatsVO;

import java.util.List;

public interface MatchService {
    
    /**
     * 创建赛事
     */
    Match createMatch(Long userId, MatchCreateDTO dto);
    
    /**
     * 获取赛事详情
     */
    MatchDetailVO getMatchDetail(Long matchId, Long currentUserId);
    
    /**
     * 获取球队赛事列表
     */
    List<Match> getTeamMatches(Long teamId);
    
    /**
     * 分页查询赛事
     */
    Page<Match> pageMatches(int page, int size, Long teamId);
    
    /**
     * 更新赛事
     */
    void updateMatch(Long matchId, Long userId, MatchCreateDTO dto);
    
    /**
     * 取消赛事
     */
    void cancelMatch(Long matchId, Long userId);
    
    /**
     * 报名参赛
     */
    void registerMatch(Long matchId, Long userId, Long teamId, MatchRegisterDTO dto);
    
    /**
     * 取消报名
     */
    void cancelRegistration(Long matchId, Long userId);
    
    /**
     * 设置首发阵容
     */
    void setLineup(Long matchId, Long userId, List<Long> starterUserIds);
    
    /**
     * 录入比赛结果
     */
    void recordResult(Long matchId, Long userId, MatchResultDTO dto);
    
    /**
     * 获取赛事统计
     */
    MatchStatsVO getMatchStats(Long matchId);
    
    /**
     * 获取球队近期赛事
     */
    List<Match> getRecentMatches(Long teamId, int limit);
}
