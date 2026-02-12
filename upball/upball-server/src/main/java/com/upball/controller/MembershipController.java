package com.upball.controller;

import com.upball.dto.MembershipPurchaseDTO;
import com.upball.entity.MembershipOrder;
import com.upball.entity.MembershipPlan;
import com.upball.service.MembershipService;
import com.upball.vo.MembershipStatusVO;
import com.upball.vo.PayResultVO;
import com.upball.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /**
     * 获取会员套餐列表
     */
    @GetMapping("/plans")
    public Result<List<MembershipPlan>> getTeamPlans(@RequestParam Long teamId) {
        List<MembershipPlan> plans = membershipService.getTeamPlans(teamId);
        return Result.success(plans);
    }

    /**
     * 购买会员
     */
    @PostMapping("/purchase")
    public Result<PayResultVO> purchase(@Valid @RequestBody MembershipPurchaseDTO dto,
                                         HttpServletRequest request) {
        Long userId = getUserId(request);
        PayResultVO vo = membershipService.purchase(userId, dto);
        return Result.success(vo);
    }

    /**
     * 获取我的会员状态
     */
    @GetMapping("/my-status")
    public Result<MembershipStatusVO> getMyStatus(@RequestParam Long teamId,
                                                   HttpServletRequest request) {
        Long userId = getUserId(request);
        MembershipStatusVO vo = membershipService.getUserMembership(teamId, userId);
        return Result.success(vo);
    }

    /**
     * 获取我的购买记录
     */
    @GetMapping("/my-orders")
    public Result<List<MembershipOrder>> getMyOrders(@RequestParam Long teamId,
                                                      HttpServletRequest request) {
        Long userId = getUserId(request);
        List<MembershipOrder> orders = membershipService.getUserOrders(teamId, userId);
        return Result.success(orders);
    }

    /**
     * 检查是否可以报名
     */
    @GetMapping("/check-register")
    public Result<Boolean> checkRegister(@RequestParam Long teamId,
                                          HttpServletRequest request) {
        Long userId = getUserId(request);
        boolean canRegister = membershipService.checkCanRegister(teamId, userId);
        return Result.success(canRegister);
    }

    /**
     * 支付回调（微信）
     */
    @PostMapping("/pay-callback")
    public Result<Void> payCallback(@RequestParam String orderNo,
                                     @RequestParam String tradeNo) {
        membershipService.handlePayCallback(orderNo, tradeNo);
        return Result.success();
    }
}
