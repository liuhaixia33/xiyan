package com.upball.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.upball.dto.MatchCreateDTO;
import com.upball.dto.MatchRegisterDTO;
import com.upball.dto.MatchResultDTO;
import com.upball.entity.Match;
import com.upball.service.MatchService;
import com.upball.vo.MatchDetailVO;
import com.upball.vo.MatchStatsVO;
import com.upball.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /**
     * 创建赛事
     */
    @PostMapping
    public Result<Match> createMatch(@Valid @RequestBody MatchCreateDTO dto,
                                      HttpServletRequest request) {
        Long userId = getUserId(request);
        Match match = matchService.createMatch(userId, dto);
        return Result.success(match);
    }

    /**
     * 获取赛事详情
     */
    @GetMapping("/{matchId}")
    public Result<MatchDetailVO> getMatchDetail(@PathVariable Long matchId,
                                                 HttpServletRequest request) {
        Long userId = getUserId(request);
        MatchDetailVO vo = matchService.getMatchDetail(matchId, userId);
        return Result.success(vo);
    }

    /**
     * 获取球队赛事列表
     */
    @GetMapping("/team/{teamId}")
    public Result<List<Match>> getTeamMatches(@PathVariable Long teamId) {
        List<Match> matches = matchService.getTeamMatches(teamId);
        return Result.success(matches);
    }

    /**
     * 分页查询赛事
     */
    @GetMapping
    public Result<Page<Match>> pageMatches(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) Long teamId) {
        Page<Match> result = matchService.pageMatches(page, size, teamId);
        return Result.success(result);
    }

    /**
     * 更新赛事
     */
    @PutMapping("/{matchId}")
    public Result<Void> updateMatch(@PathVariable Long matchId,
                                     @RequestBody MatchCreateDTO dto,
                                     HttpServletRequest request) {
        Long userId = getUserId(request);
        matchService.updateMatch(matchId, userId, dto);
        return Result.success();
    }

    /**
     * 取消赛事
     */
    @PostMapping("/{matchId}/cancel")
    public Result<Void> cancelMatch(@PathVariable Long matchId,
                                     HttpServletRequest request) {
        Long userId = getUserId(request);
        matchService.cancelMatch(matchId, userId);
        return Result.success();
    }

    /**
     * 报名参赛
     */
    @PostMapping("/{matchId}/register")
    public Result<Void> registerMatch(@PathVariable Long matchId,
                                       @RequestParam Long teamId,
                                       @Valid @RequestBody MatchRegisterDTO dto,
                                       HttpServletRequest request) {
        Long userId = getUserId(request);
        matchService.registerMatch(matchId, userId, teamId, dto);
        return Result.success();
    }

    /**
     * 取消报名
     */
    @PostMapping("/{matchId}/unregister")
    public Result<Void> cancelRegistration(@PathVariable Long matchId,
                                            HttpServletRequest request) {
        Long userId = getUserId(request);
        matchService.cancelRegistration(matchId, userId);
        return Result.success();
    }

    /**
     * 设置首发阵容
     */
    @PostMapping("/{matchId}/lineup")
    public Result<Void> setLineup(@PathVariable Long matchId,
                                   @RequestBody List<Long> starterUserIds,
                                   HttpServletRequest request) {
        Long userId = getUserId(request);
        matchService.setLineup(matchId, userId, starterUserIds);
        return Result.success();
    }

    /**
     * 录入比赛结果
     */
    @PostMapping("/{matchId}/result")
    public Result<Void> recordResult(@PathVariable Long matchId,
                                      @RequestBody MatchResultDTO dto,
                                      HttpServletRequest request) {
        Long userId = getUserId(request);
        matchService.recordResult(matchId, userId, dto);
        return Result.success();
    }

    /**
     * 获取赛事统计
     */
    @GetMapping("/{matchId}/stats")
    public Result<MatchStatsVO> getMatchStats(@PathVariable Long matchId) {
        MatchStatsVO vo = matchService.getMatchStats(matchId);
        return Result.success(vo);
    }

    /**
     * 获取近期赛事
     */
    @GetMapping("/team/{teamId}/recent")
    public Result<List<Match>> getRecentMatches(@PathVariable Long teamId,
                                                 @RequestParam(defaultValue = "5") int limit) {
        List<Match> matches = matchService.getRecentMatches(teamId, limit);
        return Result.success(matches);
    }
}
