package com.upball.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.upball.dto.TeamCreateDTO;
import com.upball.dto.TeamUpdateDTO;
import com.upball.entity.Team;
import com.upball.entity.TeamMember;
import com.upball.enums.ResultCode;
import com.upball.exception.BusinessException;
import com.upball.mapper.TeamMapper;
import com.upball.mapper.TeamMemberMapper;
import com.upball.service.TeamService;
import com.upball.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;
    
    @Autowired
    private TeamMemberMapper memberMapper;
    
    @Autowired
    private SnowflakeIdUtil idUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Team createTeam(Long userId, TeamCreateDTO dto) {
        Team team = new Team();
        team.setId(idUtil.nextId());
        BeanUtils.copyProperties(dto, team);
        team.setCaptainId(userId);
        team.setStatus(1);
        team.setMemberCount(1);
        team.setFoundedAt(LocalDate.now());
        teamMapper.insert(team);
        
        TeamMember member = new TeamMember();
        member.setId(idUtil.nextId());
        member.setTeamId(team.getId());
        member.setUserId(userId);
        member.setRole(1);
        member.setStatus(1);
        memberMapper.insert(member);
        
        log.info("创建球队成功: teamId={}, name={}", team.getId(), team.getName());
        return team;
    }

    @Override
    public Team getTeamDetail(Long teamId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null || team.getDeleted() == 1) {
            throw new BusinessException(ResultCode.TEAM_NOT_FOUND);
        }
        return team;
    }

    @Override
    public void updateTeam(Long teamId, Long userId, TeamUpdateDTO dto) {
        Team team = getTeamDetail(teamId);
        if (!hasManagePermission(teamId, userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        BeanUtils.copyProperties(dto, team);
        teamMapper.updateById(team);
    }

    @Override
    public List<Team> getUserTeams(Long userId) {
        return teamMapper.selectByUserId(userId);
    }

    @Override
    public Page<Team> pageTeams(int page, int size, String keyword) {
        Page<Team> pageParam = new Page<>(page, size);
        return teamMapper.selectPage(pageParam, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinTeam(Long teamId, Long userId) {
        Team team = getTeamDetail(teamId);
        
        TeamMember exist = memberMapper.selectByTeamAndUser(teamId, userId);
        if (exist != null && exist.getDeleted() == 0) {
            throw new BusinessException(ResultCode.MEMBER_ALREADY_EXIST);
        }
        
        TeamMember member = new TeamMember();
        member.setId(idUtil.nextId());
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setRole(3);
        member.setStatus(1);
        memberMapper.insert(member);
        
        team.setMemberCount(memberMapper.countActiveMembers(teamId));
        teamMapper.updateById(team);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quitTeam(Long teamId, Long userId) {
        Team team = getTeamDetail(teamId);
        
        if (team.getCaptainId().equals(userId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        
        TeamMember member = memberMapper.selectByTeamAndUser(teamId, userId);
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        memberMapper.deleteById(member.getId());
        
        team.setMemberCount(memberMapper.countActiveMembers(teamId));
        teamMapper.updateById(team);
    }

    @Override
    public List<TeamMember> getTeamMembers(Long teamId) {
        return memberMapper.selectByTeamId(teamId);
    }

    @Override
    public boolean isTeamMember(Long teamId, Long userId) {
        TeamMember member = memberMapper.selectByTeamAndUser(teamId, userId);
        return member != null && member.getDeleted() == 0 && member.getStatus() == 1;
    }

    @Override
    public boolean hasManagePermission(Long teamId, Long userId) {
        Team team = getTeamDetail(teamId);
        if (team.getCaptainId().equals(userId)) return true;
        if (team.getViceCaptainId() != null && team.getViceCaptainId().equals(userId)) return true;
        TeamMember member = memberMapper.selectByTeamAndUser(teamId, userId);
        return member != null && member.getRole() <= 2;
    }
}
