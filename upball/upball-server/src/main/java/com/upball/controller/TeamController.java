package com.upball.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.upball.dto.TeamCreateDTO;
import com.upball.dto.TeamUpdateDTO;
import com.upball.entity.Team;
import com.upball.entity.TeamMember;
import com.upball.service.TeamService;
import com.upball.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /**
     * 创建球队
     */
    @PostMapping
    public Result<Team> createTeam(@Valid @RequestBody TeamCreateDTO dto, 
                                    HttpServletRequest request) {
        Long userId = getUserId(request);
        Team team = teamService.createTeam(userId, dto);
        return Result.success(team);
    }

    /**
     * 获取球队详情
     */
    @GetMapping("/{teamId}")
    public Result<Team> getTeamDetail(@PathVariable Long teamId) {
        Team team = teamService.getTeamDetail(teamId);
        return Result.success(team);
    }

    /**
     * 更新球队
     */
    @PutMapping("/{teamId}")
    public Result<Void> updateTeam(@PathVariable Long teamId,
                                    @RequestBody TeamUpdateDTO dto,
                                    HttpServletRequest request) {
        Long userId = getUserId(request);
        teamService.updateTeam(teamId, userId, dto);
        return Result.success();
    }

    /**
     * 获取我的球队列表
     */
    @GetMapping("/my")
    public Result<List<Team>> getMyTeams(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Team> teams = teamService.getUserTeams(userId);
        return Result.success(teams);
    }

    /**
     * 分页查询球队
     */
    @GetMapping
    public Result<Page<Team>> pageTeams(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String keyword) {
        Page<Team> result = teamService.pageTeams(page, size, keyword);
        return Result.success(result);
    }

    /**
     * 加入球队
     */
    @PostMapping("/{teamId}/join")
    public Result<Void> joinTeam(@PathVariable Long teamId, HttpServletRequest request) {
        Long userId = getUserId(request);
        teamService.joinTeam(teamId, userId);
        return Result.success();
    }

    /**
     * 退出球队
     */
    @PostMapping("/{teamId}/quit")
    public Result<Void> quitTeam(@PathVariable Long teamId, HttpServletRequest request) {
        Long userId = getUserId(request);
        teamService.quitTeam(teamId, userId);
        return Result.success();
    }

    /**
     * 获取球队成员
     */
    @GetMapping("/{teamId}/members")
    public Result<List<TeamMember>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamMember> members = teamService.getTeamMembers(teamId);
        return Result.success(members);
    }
}
