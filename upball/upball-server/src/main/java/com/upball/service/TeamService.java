package com.upball.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.upball.dto.TeamCreateDTO;
import com.upball.dto.TeamUpdateDTO;
import com.upball.entity.Team;
import com.upball.entity.TeamMember;

import java.util.List;

public interface TeamService {
    
    /**
     * 创建球队
     */
    Team createTeam(Long userId, TeamCreateDTO dto);
    
    /**
     * 获取球队详情
     */
    Team getTeamDetail(Long teamId);
    
    /**
     * 更新球队信息
     */
    void updateTeam(Long teamId, Long userId, TeamUpdateDTO dto);
    
    /**
     * 获取用户的球队列表
     */
    List<Team> getUserTeams(Long userId);
    
    /**
     * 分页查询球队
     */
    Page<Team> pageTeams(int page, int size, String keyword);
    
    /**
     * 加入球队
     */
    void joinTeam(Long teamId, Long userId);
    
    /**
     * 退出球队
     */
    void quitTeam(Long teamId, Long userId);
    
    /**
     * 获取球队成员列表
     */
    List<TeamMember> getTeamMembers(Long teamId);
    
    /**
     * 检查用户是否是球队成员
     */
    boolean isTeamMember(Long teamId, Long userId);
    
    /**
     * 检查用户是否有管理权限（队长/副队长）
     */
    boolean hasManagePermission(Long teamId, Long userId);
}
