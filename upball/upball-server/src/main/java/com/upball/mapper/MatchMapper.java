package com.upball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upball.entity.Match;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MatchMapper extends BaseMapper<Match> {

    @Select("SELECT * FROM matches WHERE (home_team_id = #{teamId} OR away_team_id = #{teamId}) " +
            "AND deleted = 0 ORDER BY match_time DESC")
    List<Match> selectByTeamId(Long teamId);

    @Select("SELECT * FROM matches WHERE home_team_id = #{teamId} AND status = 0 AND deleted = 0 " +
            "AND match_time > NOW() ORDER BY match_time ASC LIMIT #{limit}")
    List<Match> selectUpcomingByTeamId(@Param("teamId") Long teamId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM matches WHERE (home_team_id = #{teamId} OR away_team_id = #{teamId}) " +
            "AND status = 2 AND deleted = 0")
    int countFinishedMatches(Long teamId);
}
