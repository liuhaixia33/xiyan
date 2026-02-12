package com.upball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upball.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    @Select("SELECT t.* FROM teams t " +
            "INNER JOIN team_members tm ON t.id = tm.team_id " +
            "WHERE tm.user_id = #{userId} AND t.deleted = 0 AND tm.deleted = 0")
    List<Team> selectByUserId(Long userId);
}
