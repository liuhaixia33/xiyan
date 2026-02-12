package com.upball.service;

import com.upball.dto.MembershipPurchaseDTO;
import com.upball.entity.MembershipOrder;
import com.upball.entity.MembershipPlan;
import com.upball.vo.MembershipStatusVO;
import com.upball.vo.PayResultVO;

import java.util.List;

public interface MembershipService {
    
    /**
     * 获取球队的会员套餐
     */
    List<MembershipPlan> getTeamPlans(Long teamId);
    
    /**
     * 购买会员
     */
    PayResultVO purchase(Long userId, MembershipPurchaseDTO dto);
    
    /**
     * 获取用户的会员状态
     */
    MembershipStatusVO getUserMembership(Long teamId, Long userId);
    
    /**
     * 获取用户的购买记录
     */
    List<MembershipOrder> getUserOrders(Long teamId, Long userId);
    
    /**
     * 检查用户是否可以报名
     */
    boolean checkCanRegister(Long teamId, Long userId);
    
    /**
     * 使用次数（报名成功后调用）
     */
    void deductTimes(Long teamId, Long userId, Long matchId);
    
    /**
     * 支付回调处理
     */
    void handlePayCallback(String orderNo, String tradeNo);
}
