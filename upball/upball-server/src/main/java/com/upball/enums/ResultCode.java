package com.upball.enums;

import lombok.Getter;

@Getter
public enum ResultCode {
    
    SUCCESS(200, "成功"),
    ERROR(500, "服务器错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 业务错误码 1000-1999
    TEAM_NOT_FOUND(1001, "球队不存在"),
    TEAM_ALREADY_EXIST(1002, "球队已存在"),
    MEMBER_ALREADY_EXIST(1003, "已是球队成员"),
    MEMBER_NOT_FOUND(1004, "不是球队成员"),
    NO_PERMISSION(1005, "无权限操作"),
    
    // 会员错误码 2000-2999
    MEMBERSHIP_EXPIRED(2001, "会员已过期"),
    MEMBERSHIP_NO_TIMES(2002, "剩余次数不足"),
    PLAN_NOT_FOUND(2003, "套餐不存在"),
    ORDER_NOT_FOUND(2004, "订单不存在"),
    ORDER_ALREADY_PAID(2005, "订单已支付"),
    
    // 赛事错误码 3000-3999
    MATCH_NOT_FOUND(3001, "赛事不存在"),
    MATCH_ALREADY_STARTED(3002, "赛事已开始"),
    ALREADY_REGISTERED(3003, "已报名");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
