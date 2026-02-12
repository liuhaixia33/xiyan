package com.upball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upball.entity.MatchAttendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MatchAttendanceMapper extends BaseMapper<MatchAttendance> {

    @Select("SELECT * FROM match_attendance WHERE match_id = #{matchId} AND deleted = 0")
    List<MatchAttendance> selectByMatchId(Long matchId);

    @Select("SELECT * FROM match_attendance WHERE match_id = #{matchId} AND team_id = #{teamId} AND deleted = 0")
    List<MatchAttendance> selectByMatchAndTeam(@Param("matchId") Long matchId, @Param("teamId") Long teamId);

    @Select("SELECT * FROM match_attendance WHERE match_id = #{matchId} AND user_id = #{userId} AND deleted = 0")
    MatchAttendance selectByMatchAndUser(@Param("matchId") Long matchId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM match_attendance WHERE match_id = #{matchId} AND status = 1 AND deleted = 0")
    int countConfirmed(Long matchId);

    @Select("SELECT COUNT(*) FROM match_attendance WHERE match_id = #{matchId} AND status = 2 AND deleted = 0")
    int countPending(Long matchId);

    @Select("SELECT COUNT(*) FROM match_attendance WHERE match_id = #{matchId} AND status = 3 AND deleted = 0")
    int countDeclined(Long matchId);

    @Update("UPDATE match_attendance SET is_starter = #{isStarter} WHERE id = #{id}")
    int updateStarterStatus(@Param("id") Long id, @Param("isStarter") Integer isStarter);

    @Select("SELECT COUNT(*) FROM match_attendance WHERE match_id = #{matchId} AND user_id = #{userId} AND deleted = 0")
    int checkUserRegistered(@Param("matchId") Long matchId, @Param("userId") Long userId);
}
