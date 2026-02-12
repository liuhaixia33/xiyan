package com.upball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upball.entity.MembershipPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MembershipPlanMapper extends BaseMapper<MembershipPlan> {

    @Select("SELECT * FROM membership_plans WHERE team_id = #{teamId} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<MembershipPlan> selectActiveByTeamId(Long teamId);
}
