package com.upball.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.upball.dto.MatchCreateDTO;
import com.upball.dto.MatchRegisterDTO;
import com.upball.dto.MatchResultDTO;
import com.upball.entity.Match;
import com.upball.entity.MatchAttendance;
import com.upball.entity.Team;
import com.upball.entity.TeamMember;
import com.upball.enums.ResultCode;
import com.upball.exception.BusinessException;
import com.upball.mapper.MatchAttendanceMapper;
import com.upball.mapper.MatchMapper;
import com.upball.mapper.TeamMapper;
import com.upball.mapper.TeamMemberMapper;
import com.upball.service.MembershipService;
import com.upball.service.MatchService;
import com.upball.service.TeamService;
import com.upball.utils.SnowflakeIdUtil;
import com.upball.vo.MatchAttendanceVO;
import com.upball.vo.MatchDetailVO;
import com.upball.vo.MatchStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchMapper matchMapper;
    
    @Autowired
    private MatchAttendanceMapper attendanceMapper;
    
    @Autowired
    private TeamMapper teamMapper;
    
    @Autowired
    private TeamMemberMapper memberMapper;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private MembershipService membershipService;
    
    @Autowired
    private SnowflakeIdUtil idUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Match createMatch(Long userId, MatchCreateDTO dto) {
        // 检查权限
        if (!teamService.hasManagePermission(dto.getHomeTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        Match match = new Match();
        match.setId(idUtil.nextId());
        BeanUtils.copyProperties(dto, match);
        match.setStatus(0); // 未开始
        match.setHomeScore(0);
        match.setAwayScore(0);
        match.setCreatedBy(userId);
        matchMapper.insert(match);
        
        log.info("创建赛事: matchId={}, title={}, teamId={}", match.getId(), match.getTitle(), dto.getHomeTeamId());
        return match;
    }

    @Override
    public MatchDetailVO getMatchDetail(Long matchId, Long currentUserId) {
        Match match = matchMapper.selectById(matchId);
        if (match == null || match.getDeleted() == 1) {
            throw new BusinessException(ResultCode.MATCH_NOT_FOUND);
        }
        
        MatchDetailVO vo = new MatchDetailVO();
        BeanUtils.copyProperties(match, vo);
        
        // 查询球队名称
        Team homeTeam = teamMapper.selectById(match.getHomeTeamId());
        if (homeTeam != null) {
            vo.setHomeTeamName(homeTeam.getName());
            vo.setHomeTeamLogo(homeTeam.getLogo());
        }
        
        if (match.getAwayTeamId() != null) {
            Team awayTeam = teamMapper.selectById(match.getAwayTeamId());
            if (awayTeam != null) {
                vo.setAwayTeamName(awayTeam.getName());
                vo.setAwayTeamLogo(awayTeam.getLogo());
            }
        }
        
        // 统计报名人数
        vo.setConfirmedCount(attendanceMapper.countConfirmed(matchId));
        vo.setPendingCount(attendanceMapper.countPending(matchId));
        vo.setDeclinedCount(attendanceMapper.countDeclined(matchId));
        
        // 当前用户报名状态
        if (currentUserId != null) {
            MatchAttendance myAttendance = attendanceMapper.selectByMatchAndUser(matchId, currentUserId);
            if (myAttendance != null) {
                vo.setMyStatus(myAttendance.getStatus());
            }
        }
        
        return vo;
    }

    @Override
    public List<Match> getTeamMatches(Long teamId) {
        return matchMapper.selectByTeamId(teamId);
    }

    @Override
    public Page<Match> pageMatches(int page, int size, Long teamId) {
        Page<Match> pageParam = new Page<>(page, size);
        // TODO: 完善查询条件
        return matchMapper.selectPage(pageParam, null);
    }

    @Override
    public void updateMatch(Long matchId, Long userId, MatchCreateDTO dto) {
        Match match = matchMapper.selectById(matchId);
        if (match == null || match.getDeleted() == 1) {
            throw new BusinessException(ResultCode.MATCH_NOT_FOUND);
        }
        
        // 检查权限
        if (!teamService.hasManagePermission(match.getHomeTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        // 比赛已开始不能修改
        if (match.getStatus() != 0) {
            throw new BusinessException(ResultCode.MATCH_ALREADY_STARTED);
        }
        
        BeanUtils.copyProperties(dto, match);
        matchMapper.updateById(match);
    }

    @Override
    public void cancelMatch(Long matchId, Long userId) {
        Match match = matchMapper.selectById(matchId);
        if (match == null || match.getDeleted() == 1) {
            throw new BusinessException(ResultCode.MATCH_NOT_FOUND);
        }
        
        if (!teamService.hasManagePermission(match.getHomeTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        match.setStatus(3); // 取消
        matchMapper.updateById(match);
        
        log.info("取消赛事: matchId={}", matchId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerMatch(Long matchId, Long userId, Long teamId, MatchRegisterDTO dto) {
        Match match = matchMapper.selectById(matchId);
        if (match == null || match.getDeleted() == 1) {
            throw new BusinessException(ResultCode.MATCH_NOT_FOUND);
        }
        
        // 检查是否是球队成员
        if (!teamService.isTeamMember(teamId, userId)) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 检查会员资格
        if (dto.getStatus() == 1) { // 确认参加
            boolean canRegister = membershipService.checkCanRegister(teamId, userId);
            if (!canRegister) {
                throw new BusinessException(ResultCode.MEMBERSHIP_EXPIRED);
            }
        }
        
        // 检查是否已报名
        MatchAttendance exist = attendanceMapper.selectByMatchAndUser(matchId, userId);
        if (exist != null && exist.getDeleted() == 0) {
            // 更新状态
            exist.setStatus(dto.getStatus());
            exist.setComment(dto.getComment());
            attendanceMapper.updateById(exist);
        } else {
            // 新增报名
            MatchAttendance attendance = new MatchAttendance();
            attendance.setId(idUtil.nextId());
            attendance.setMatchId(matchId);
            attendance.setTeamId(teamId);
            attendance.setUserId(userId);
            attendance.setStatus(dto.getStatus());
            attendance.setComment(dto.getComment());
            attendance.setIsStarter(0);
            attendance.setGoals(0);
            attendance.setAssists(0);
            attendance.setYellowCards(0);
            attendance.setRedCards(0);
            attendanceMapper.insert(attendance);
        }
        
        log.info("赛事报名: matchId={}, userId={}, status={}", matchId, userId, dto.getStatus());
    }

    @Override
    public void cancelRegistration(Long matchId, Long userId) {
        MatchAttendance attendance = attendanceMapper.selectByMatchAndUser(matchId, userId);
        if (attendance == null || attendance.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        attendanceMapper.deleteById(attendance.getId());
        log.info("取消报名: matchId={}, userId={}", matchId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setLineup(Long matchId, Long userId, List<Long> starterUserIds) {
        Match match = matchMapper.selectById(matchId);
        if (match == null || match.getDeleted() == 1) {
            throw new BusinessException(ResultCode.MATCH_NOT_FOUND);
        }
        
        if (!teamService.hasManagePermission(match.getHomeTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        // 重置所有为替补
        List<MatchAttendance> attendances = attendanceMapper.selectByMatchId(matchId);
        for (MatchAttendance attendance : attendances) {
            if (attendance.getStatus() == 1) { // 只有确认参加的才能设置首发
                int isStarter = starterUserIds.contains(attendance.getUserId()) ? 1 : 0;
                attendanceMapper.updateStarterStatus(attendance.getId(), isStarter);
            }
        }
        
        log.info("设置首发: matchId={}, starters={}", matchId, starterUserIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordResult(Long matchId, Long userId, MatchResultDTO dto) {
        Match match = matchMapper.selectById(matchId);
        if (match == null || match.getDeleted() == 1) {
            throw new BusinessException(ResultCode.MATCH_NOT_FOUND);
        }
        
        if (!teamService.hasManagePermission(match.getHomeTeamId(), userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        // 更新比分
        match.setHomeScore(dto.getHomeScore());
        match.setAwayScore(dto.getAwayScore());
        match.setStatus(2); // 已结束
        matchMapper.updateById(match);
        
        // 更新球员数据
        if (dto.getPlayerStats() != null) {
            for (MatchResultDTO.PlayerStatsDTO stats : dto.getPlayerStats()) {
                MatchAttendance attendance = attendanceMapper.selectByMatchAndUser(matchId, stats.getUserId());
                if (attendance != null) {
                    attendance.setGoals(stats.getGoals());
                    attendance.setAssists(stats.getAssists());
                    attendance.setYellowCards(stats.getYellowCards());
                    attendance.setRedCards(stats.getRedCards());
                    attendance.setRating(stats.getRating());
                    attendanceMapper.updateById(attendance);
                }
            }
        }
        
        // 扣除会员次数（对主队成员）
        List<MatchAttendance> homeAttendances = attendanceMapper.selectByMatchAndTeam(matchId, match.getHomeTeamId());
        for (MatchAttendance attendance : homeAttendances) {
            if (attendance.getStatus() == 1) { // 参加的人
                try {
                    membershipService.deductTimes(match.getHomeTeamId(), attendance.getUserId(), matchId);
                } catch (Exception e) {
                    log.warn("扣除次数失败: userId={}, error={}", attendance.getUserId(), e.getMessage());
                }
            }
        }
        
        log.info("录入比赛结果: matchId={}, score={}:{}", matchId, dto.getHomeScore(), dto.getAwayScore());
    }

    @Override
    public MatchStatsVO getMatchStats(Long matchId) {
        Match match = matchMapper.selectById(matchId);
        if (match == null) {
            throw new BusinessException(ResultCode.MATCH_NOT_FOUND);
        }
        
        MatchStatsVO vo = new MatchStatsVO();
        vo.setMatchId(matchId);
        
        List<MatchAttendance> attendances = attendanceMapper.selectByMatchId(matchId);
        
        vo.setConfirmed((int) attendances.stream().filter(a -> a.getStatus() == 1).count());
        vo.setPending((int) attendances.stream().filter(a -> a.getStatus() == 2).count());
        vo.setDeclined((int) attendances.stream().filter(a -> a.getStatus() == 3).count());
        vo.setStarterCount((int) attendances.stream().filter(a -> a.getIsStarter() == 1).count());
        vo.setSubstituteCount((int) attendances.stream().filter(a -> a.getStatus() == 1 && a.getIsStarter() == 0).count());
        
        vo.setTotalGoals(attendances.stream().mapToInt(MatchAttendance::getGoals).sum());
        vo.setTotalAssists(attendances.stream().mapToInt(MatchAttendance::getAssists).sum());
        vo.setTotalYellowCards(attendances.stream().mapToInt(MatchAttendance::getYellowCards).sum());
        vo.setTotalRedCards(attendances.stream().mapToInt(MatchAttendance::getRedCards).sum());
        
        return vo;
    }

    @Override
    public List<Match> getRecentMatches(Long teamId, int limit) {
        return matchMapper.selectUpcomingByTeamId(teamId, limit);
    }
}
