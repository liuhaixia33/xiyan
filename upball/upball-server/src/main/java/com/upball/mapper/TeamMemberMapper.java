package com.upball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upball.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    @Select("SELECT * FROM team_members WHERE team_id = #{teamId} AND deleted = 0 ORDER BY role ASC, created_at ASC")
    List<TeamMember> selectByTeamId(Long teamId);

    @Select("SELECT * FROM team_members WHERE team_id = #{teamId} AND user_id = #{userId} AND deleted = 0")
    TeamMember selectByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Update("UPDATE team_members SET status = 0 WHERE id = #{id}")
    int quitTeam(Long id);

    @Select("SELECT COUNT(*) FROM team_members WHERE team_id = #{teamId} AND status = 1 AND deleted = 0")
    int countActiveMembers(Long teamId);
}
