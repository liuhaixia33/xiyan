package com.upball.vo;

import lombok.Data;

@Data
public class PayResultVO {
    
    private String orderNo;
    private String payUrl;
    private Long expireSeconds;
}
