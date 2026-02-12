package com.upball.service.impl;

import com.upball.dto.MembershipPurchaseDTO;
import com.upball.entity.MembershipOrder;
import com.upball.entity.MembershipPlan;
import com.upball.enums.ResultCode;
import com.upball.exception.BusinessException;
import com.upball.mapper.MembershipOrderMapper;
import com.upball.mapper.MembershipPlanMapper;
import com.upball.service.MembershipService;
import com.upball.utils.SnowflakeIdUtil;
import com.upball.vo.MembershipOrderVO;
import com.upball.vo.MembershipStatusVO;
import com.upball.vo.PayResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MembershipServiceImpl implements MembershipService {

    @Autowired
    private MembershipPlanMapper planMapper;
    
    @Autowired
    private MembershipOrderMapper orderMapper;
    
    @Autowired
    private SnowflakeIdUtil idUtil;

    @Override
    public List<MembershipPlan> getTeamPlans(Long teamId) {
        return planMapper.selectActiveByTeamId(teamId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultVO purchase(Long userId, MembershipPurchaseDTO dto) {
        MembershipPlan plan = planMapper.selectById(dto.getPlanId());
        if (plan == null || plan.getDeleted() == 1 || plan.getStatus() != 1) {
            throw new BusinessException(ResultCode.PLAN_NOT_FOUND);
        }
        
        // 生成订单号
        String orderNo = "M" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
                + System.currentTimeMillis() % 1000000;
        
        MembershipOrder order = new MembershipOrder();
        order.setId(idUtil.nextId());
        order.setOrderNo(orderNo);
        order.setTeamId(dto.getTeamId());
        order.setUserId(userId);
        order.setPlanId(plan.getId());
        order.setPlanName(plan.getName());
        order.setPlanType(plan.getType());
        order.setPlanLevel(plan.getLevel());
        order.setAmount(plan.getPrice());
        order.setPayType(dto.getPayType());
        order.setPayStatus(0);
        order.setLevel(plan.getLevel());
        
        // 次卡
        if (plan.getType() == 1) {
            order.setTotalTimes(plan.getTimes());
            order.setRemainingTimes(plan.getTimes());
            order.setUsedTimes(0);
            order.setValidStartDate(LocalDate.now());
        } else {
            // 周期卡
            order.setValidStartDate(LocalDate.now());
            order.setValidEndDate(LocalDate.now().plusMonths(plan.getDurationMonths()));
        }
        
        order.setStatus(1);
        orderMapper.insert(order);
        
        // TODO: 调用微信支付
        PayResultVO vo = new PayResultVO();
        vo.setOrderNo(orderNo);
        vo.setPayUrl("weixin://"); // 模拟
        vo.setExpireSeconds(3600L);
        
        log.info("创建会员订单: orderNo={}, userId={}, amount={}", orderNo, userId, plan.getPrice());
        return vo;
    }

    @Override
    public MembershipStatusVO getUserMembership(Long teamId, Long userId) {
        List<MembershipOrder> activeOrders = orderMapper.selectActiveByUser(teamId, userId);
        
        MembershipStatusVO vo = new MembershipStatusVO();
        vo.setTeamId(teamId);
        vo.setUserId(userId);
        
        if (activeOrders.isEmpty()) {
            vo.setLevel(0);
            vo.setLevelName("平民");
            vo.setValid(false);
            vo.setRemainingTimes(0);
            vo.setBenefits(new ArrayList<>());
            return vo;
        }
        
        // 检查VIP
        MembershipOrder vipOrder = activeOrders.stream()
                .filter(o -> o.getPlanType() == 2)
                .filter(o -> o.getValidEndDate() != null && o.getValidEndDate().isAfter(LocalDate.now()))
                .max(Comparator.comparing(MembershipOrder::getLevel))
                .orElse(null);
        
        if (vipOrder != null) {
            vo.setLevel(3);
            vo.setLevelName("VIP");
            vo.setValid(true);
            vo.setExpireDate(vipOrder.getValidEndDate());
            vo.setBenefits(Arrays.asList("无限次参赛", "优先报名", "装备8折", "专属队服"));
        } else {
            // 统计次卡
            int totalRemaining = activeOrders.stream()
                    .filter(o -> o.getPlanType() == 1)
                    .filter(o -> o.getRemainingTimes() != null && o.getRemainingTimes() > 0)
                    .mapToInt(MembershipOrder::getRemainingTimes)
                    .sum();
            
            if (totalRemaining > 0) {
                int maxLevel = activeOrders.stream()
                        .filter(o -> o.getRemainingTimes() != null && o.getRemainingTimes() > 0)
                        .map(MembershipOrder::getLevel)
                        .max(Integer::compare)
                        .orElse(0);
                
                vo.setLevel(maxLevel);
                vo.setLevelName(maxLevel == 2 ? "黄金会员" : "白银会员");
                vo.setValid(true);
                vo.setRemainingTimes(totalRemaining);
                vo.setBenefits(maxLevel == 2 
                        ? Arrays.asList("20次参赛资格", "装备9折", "优先替补")
                        : Arrays.asList("10次参赛资格", "基础装备折扣"));
            } else {
                vo.setLevel(0);
                vo.setLevelName("平民");
                vo.setValid(false);
                vo.setRemainingTimes(0);
                vo.setBenefits(new ArrayList<>());
            }
        }
        
        // 转换订单列表
        List<MembershipOrderVO> orderVOs = activeOrders.stream()
                .filter(o -> o.getStatus() == 1)
                .map(this::convertToVO)
                .collect(Collectors.toList());
        vo.setActiveOrders(orderVOs);
        
        return vo;
    }

    @Override
    public List<MembershipOrder> getUserOrders(Long teamId, Long userId) {
        return orderMapper.selectActiveByUser(teamId, userId);
    }

    @Override
    public boolean checkCanRegister(Long teamId, Long userId) {
        MembershipStatusVO status = getUserMembership(teamId, userId);
        return status.getValid();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductTimes(Long teamId, Long userId, Long matchId) {
        MembershipStatusVO status = getUserMembership(teamId, userId);
        
        if (!status.getValid()) {
            throw new BusinessException(ResultCode.MEMBERSHIP_EXPIRED);
        }
        
        // VIP不扣次数
        if (status.getLevel() == 3) {
            return;
        }
        
        List<MembershipOrder> orders = orderMapper.selectAvailableTimesOrders(teamId, userId);
        if (orders.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBERSHIP_NO_TIMES);
        }
        
        // 使用第一个卡的次数
        MembershipOrder order = orders.get(0);
        orderMapper.deductTimes(order.getId());
        
        log.info("扣除次数: orderId={}, userId={}, matchId={}", order.getId(), userId, matchId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePayCallback(String orderNo, String tradeNo) {
        MembershipOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.error("订单不存在: {}", orderNo);
            return;
        }
        
        if (order.getPayStatus() == 1) {
            log.warn("订单已支付: {}", orderNo);
            return;
        }
        
        order.setPayStatus(1);
        order.setTradeNo(tradeNo);
        orderMapper.updateById(order);
        
        log.info("支付成功: orderNo={}, tradeNo={}", orderNo, tradeNo);
    }
    
    private MembershipOrderVO convertToVO(MembershipOrder order) {
        MembershipOrderVO vo = new MembershipOrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setOrderId(order.getId());
        return vo;
    }
}
