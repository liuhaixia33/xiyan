package com.upball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upball.entity.MembershipOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MembershipOrderMapper extends BaseMapper<MembershipOrder> {

    @Select("SELECT * FROM membership_orders WHERE order_no = #{orderNo} AND deleted = 0")
    MembershipOrder selectByOrderNo(String orderNo);

    @Select("SELECT * FROM membership_orders WHERE team_id = #{teamId} AND user_id = #{userId} " +
            "AND pay_status = 1 AND status = 1 AND deleted = 0 " +
            "ORDER BY created_at DESC")
    List<MembershipOrder> selectActiveByUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Select("SELECT * FROM membership_orders WHERE team_id = #{teamId} AND user_id = #{userId} " +
            "AND pay_status = 1 AND status = 1 AND plan_type = 1 AND remaining_times > 0 " +
            "AND deleted = 0 ORDER BY plan_level ASC, created_at ASC")
    List<MembershipOrder> selectAvailableTimesOrders(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Update("UPDATE membership_orders SET used_times = used_times + 1, " +
            "remaining_times = remaining_times - 1, " +
            "status = CASE WHEN remaining_times - 1 <= 0 THEN 2 ELSE status END " +
            "WHERE id = #{id}")
    int deductTimes(Long id);
}
